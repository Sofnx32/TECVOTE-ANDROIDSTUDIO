package pe.tecvote.enrolamiento.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.tecvote.enrolamiento.ui.*
import pe.tecvote.enrolamiento.viewmodels.EnrolamientoFlowViewModel
import pe.tecvote.enrolamiento.viewmodels.EnrolamientoFlowViewModelFactory

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainEnrolamientoFlow(navController: NavHostController = rememberNavController()) {

    val flowViewModel: EnrolamientoFlowViewModel = viewModel(
        factory = EnrolamientoFlowViewModelFactory()
    )

    val dniActual by flowViewModel.dniActual.collectAsState()
    val electorActual by flowViewModel.electorActual.collectAsState()

    var tokenObtenido by remember { mutableStateOf<String?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val rutasConBottomBar = listOf(
        RutasNavegacion.BIENVENIDA,
        RutasNavegacion.MIS_DATOS,
        "informacion",
        "ayuda"
    )
    val mostrarBottomBar = currentRoute in rutasConBottomBar

    val pantallaSeleccionada = when (currentRoute) {
        RutasNavegacion.BIENVENIDA -> "inicio"
        RutasNavegacion.MIS_DATOS -> "misdatos"
        "informacion" -> "informacion"
        "ayuda" -> "ayuda"
        else -> "inicio"
    }

    BackHandler(enabled = navController.previousBackStackEntry != null) {
        navController.popBackStack()
    }

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF020B18),
                    contentColor = Color.White,
                    tonalElevation = 12.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio", fontSize = 10.sp) },
                        selected = pantallaSeleccionada == "inicio",
                        onClick = {
                            navController.navigate(RutasNavegacion.BIENVENIDA) {
                                popUpTo(RutasNavegacion.BIENVENIDA) { inclusive = true }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00C8FF),
                            selectedTextColor = Color(0xFF00C8FF),
                            unselectedIconColor = Color.White.copy(0.5f),
                            unselectedTextColor = Color.White.copy(0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Mis datos") },
                        label = { Text("Mis datos", fontSize = 10.sp) },
                        selected = pantallaSeleccionada == "misdatos",
                        onClick = {
                            if (dniActual.isNotEmpty()) {
                                navController.navigate(RutasNavegacion.misDatos(dniActual)) {
                                    popUpTo(RutasNavegacion.BIENVENIDA) { inclusive = false }
                                }
                            }
                        },
                        enabled = dniActual.isNotEmpty(),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00C8FF),
                            selectedTextColor = Color(0xFF00C8FF),
                            unselectedIconColor = Color.White.copy(0.5f),
                            unselectedTextColor = Color.White.copy(0.5f),
                            indicatorColor = Color.Transparent,
                            disabledIconColor = Color.White.copy(0.2f),
                            disabledTextColor = Color.White.copy(0.2f)
                        )
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = "Información") },
                        label = { Text("Información", fontSize = 10.sp) },
                        selected = pantallaSeleccionada == "informacion",
                        onClick = {
                            navController.navigate("informacion") {
                                popUpTo(RutasNavegacion.BIENVENIDA) { inclusive = false }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00C8FF),
                            selectedTextColor = Color(0xFF00C8FF),
                            unselectedIconColor = Color.White.copy(0.5f),
                            unselectedTextColor = Color.White.copy(0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Help, contentDescription = "Ayuda") },
                        label = { Text("Ayuda", fontSize = 10.sp) },
                        selected = pantallaSeleccionada == "ayuda",
                        onClick = {
                            navController.navigate("ayuda") {
                                popUpTo(RutasNavegacion.BIENVENIDA) { inclusive = false }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00C8FF),
                            selectedTextColor = Color(0xFF00C8FF),
                            unselectedIconColor = Color.White.copy(0.5f),
                            unselectedTextColor = Color.White.copy(0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = RutasNavegacion.BIENVENIDA,
            modifier = Modifier.fillMaxSize()
        ) {
            // 🔹 INICIO - Dashboard principal
            composable(RutasNavegacion.BIENVENIDA) {
                Column(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    SlideInicio(
                        onIniciarEnrolamiento = {
                            navController.navigate(RutasNavegacion.INGRESO_DNI)
                        },
                        onVerMisDatos = {
                            if (dniActual.isNotEmpty()) {
                                navController.navigate(RutasNavegacion.misDatos(dniActual))
                            }
                        }
                    )
                }
            }

            // 🔹 Flujo de enrolamiento (SIN bottom bar)
            composable(RutasNavegacion.INGRESO_DNI) {
                SlideIngresoDNI(
                    onContinuar = { dniIngresado ->
                        flowViewModel.setDni(dniIngresado)
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
                        flowViewModel.consultarElector(dni)
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
                    datosElector = electorActual,
                    onContinuar = {
                        navController.navigate(RutasNavegacion.misDatos(dni))
                    },
                    onCancelar = {
                        navController.popBackStack()
                    }
                )
            }

            // 🔹 MIS DATOS - Credencial electoral
            composable(
                route = RutasNavegacion.MIS_DATOS,
                arguments = listOf(navArgument("dni") { type = NavType.StringType })
            ) { backStackEntry ->
                val dni = backStackEntry.arguments?.getString("dni") ?: ""

                val datosViewModel: MisDatosViewModel = viewModel()
                val state by datosViewModel.state.collectAsState()

                LaunchedEffect(dni) {
                    if (dni.isNotEmpty()) {
                        datosViewModel.consultarServidor(dni)
                    }
                }

                Column(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
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
                                        // Pendiente implementar
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

            // 🔹 INFORMACIÓN
            composable("informacion") {
                Column(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    SlideInformacion()
                }
            }

            // 🔹 AYUDA
            composable("ayuda") {
                Column(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    SlideAyuda()
                }
            }


        }
    }
}