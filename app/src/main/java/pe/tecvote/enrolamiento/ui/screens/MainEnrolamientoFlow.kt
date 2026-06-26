package pe.tecvote.enrolamiento.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.ClienteRed
import pe.tecvote.enrolamiento.data.RespuestaMisDatos
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.tecvote.enrolamiento.ui.*

sealed class MisDatosUiState {
    data object Cargando : MisDatosUiState()
    data class Exito(val datos: RespuestaMisDatos) : MisDatosUiState()
    data class Error(val mensaje: String) : MisDatosUiState()
}

class MisDatosViewModel : ViewModel() {
    private val _state = MutableStateFlow<MisDatosUiState>(MisDatosUiState.Cargando)
    val state: StateFlow<MisDatosUiState> = _state.asStateFlow()

    fun consultarServidor(dni: String) {
        viewModelScope.launch {
            _state.value = MisDatosUiState.Cargando
            Log.d("TECVOTE_NET", "Iniciando consulta HTTP de padrón para DNI: $dni")
            try {
                val response = ClienteRed.api.getMisDatosElector(dni = dni)

                Log.d("TECVOTE_NET", "Respuesta recibida del servidor central. Status: ${response.status}")

                if (response.status == "success") {
                    Log.d("TECVOTE_NET", "Mapeo exitoso. Datos de elector vinculados: ${response.elector?.nombreCompleto}")
                    _state.value = MisDatosUiState.Exito(response)
                } else {
                    Log.w("TECVOTE_NET", "Servidor Django denegó la solicitud: ${response.mensajeLogistica}")
                    _state.value = MisDatosUiState.Error(
                        response.mensajeLogistica ?: "Error en el padrón electoral."
                    )
                }
            } catch (e: Exception) {
                Log.e("TECVOTE_NET", "Excepción crítica en la capa de red al consultar DNI $dni", e)
                _state.value = MisDatosUiState.Error(
                    "Fallo de conexión institucional: ${e.localizedMessage}"
                )
            }
        }
    }
}

@Composable
fun MainEnrolamientoFlow(navController: NavHostController = rememberNavController()) {

    var dniActual by remember { mutableStateOf("") }
    var tokenObtenido by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = navController.previousBackStackEntry != null) {
        navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = RutasNavegacion.BIENVENIDA
    ) {
        composable(RutasNavegacion.BIENVENIDA) {
            SlideBienvenida(
                onContinuar = {
                    navController.navigate(RutasNavegacion.INGRESO_DNI)
                }
            )
        }

        // ── REEMPLAZA ESTE BLOQUE EN TU MainEnrolamientoFlow ──────────────────────────

        composable(RutasNavegacion.INGRESO_DNI) {
            SlideIngresoDNI(
                onContinuar = { dniIngresado ->
                    dniActual = dniIngresado

                    navController.navigate(RutasNavegacion.preguntas(dniIngresado)) {
                        popUpTo(RutasNavegacion.INGRESO_DNI) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = RutasNavegacion.PREGUNTAS,
            arguments = listOf(navArgument("dni") { type = NavType.StringType })
        ) { backStackEntry ->
            val dni = backStackEntry.arguments?.getString("dni") ?: ""
            SlidePreguntas(
                dni = dni,
                onContinuar = {
                    navController.navigate(RutasNavegacion.biometrica(dni))
                }
            )
        }

        composable(
            route = RutasNavegacion.BIOMETRICA,
            arguments = listOf(navArgument("dni") { type = NavType.StringType })
        ) { backStackEntry ->
            val dni = backStackEntry.arguments?.getString("dni") ?: ""
            SlideBiometrica(
                dni = dni,
                onContinuar = { token ->
                    if (token != null) {
                        tokenObtenido = token
                    }
                    navController.navigate(RutasNavegacion.seleccionLocalidad(dni))
                }
            )
        }

        composable(
            route = RutasNavegacion.SELECCION_LOCALIDAD,
            arguments = listOf(navArgument("dni") { type = NavType.StringType })
        ) { backStackEntry ->
            val dni = backStackEntry.arguments?.getString("dni") ?: ""
            SlideSeleccionLocalidad(
                dni = dni,
                onContinuar = {
                    navController.navigate(RutasNavegacion.gestionEnrolamiento(dni))
                }
            )
        }

        composable(
            route = RutasNavegacion.GESTION_ENROLAMIENTO,
            arguments = listOf(navArgument("dni") { type = NavType.StringType })
        ) { backStackEntry ->
            val dni = backStackEntry.arguments?.getString("dni") ?: ""
            SlideGestionEnrolamiento(
                dni = dni,
                onContinuar = {
                    navController.navigate(RutasNavegacion.misDatos(dni))
                },
                onCancelar = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = RutasNavegacion.MIS_DATOS,
            arguments = listOf(navArgument("dni") { type = NavType.StringType })
        ) { backStackEntry ->
            val dni = backStackEntry.arguments?.getString("dni") ?: ""

            val datosViewModel: MisDatosViewModel = viewModel()
            val state by datosViewModel.state.collectAsState()

            // Control de ciclo de vida corregido para evitar peticiones repetitivas
            LaunchedEffect(dni) {
                if (dni.isNotEmpty()) {
                    datosViewModel.consultarServidor(dni)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val currentState = state) {
                    is MisDatosUiState.Cargando -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    is MisDatosUiState.Exito -> {
                        SlideMisDatosCompleto(
                            datos = currentState.datos,
                            onDescargarConstancia = {
                                // Pendiente implementar lógica de descarga
                            }
                        )
                    }

                    is MisDatosUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ERROR DE SINCRONIZACIÓN",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentState.mensaje,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}