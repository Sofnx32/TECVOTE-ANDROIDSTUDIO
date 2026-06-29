package pe.tecvote.enrolamiento.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import pe.tecvote.enrolamiento.R
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
    data class Error(val mensajeResId: Int) : MisDatosUiState()
}

class MisDatosViewModel : ViewModel() {
    private val _state = MutableStateFlow<MisDatosUiState>(MisDatosUiState.Cargando)
    val state: StateFlow<MisDatosUiState> = _state.asStateFlow()

    fun consultarServidor(dni: String) {
        viewModelScope.launch {
            _state.value = MisDatosUiState.Cargando
            try {
                val response = ClienteRed.api.getMisDatosElector(dni = dni)
                if (response.status == "success") {
                    _state.value = MisDatosUiState.Exito(response)
                } else {
                    _state.value = MisDatosUiState.Error(R.string.error_sincronizacion)
                }
            } catch (e: Exception) {
                _state.value = MisDatosUiState.Error(R.string.sin_conexion_tecvote)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainEnrolamientoFlow(
    navController: NavHostController = rememberNavController(),
    onLogout: () -> Unit = {}
) {
    val flowViewModel: EnrolamientoFlowViewModel = viewModel(
        factory = EnrolamientoFlowViewModelFactory()
    )

    val dniActual by flowViewModel.dniActual.collectAsState()
    val electorActual by flowViewModel.electorActual.collectAsState()
    var showSettingsMenu by remember { mutableStateOf(false) }

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

    val azulMuyOscuroStart = Color(0xFF041120)
    val azulProfundoEnd = Color(0xFF020B18)

    val degradeFondoGlobal = Brush.verticalGradient(
        colors = listOf(
            azulMuyOscuroStart,
            azulProfundoEnd
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(degradeFondoGlobal),
        containerColor = Color.Transparent,
        topBar = {
            if (mostrarBottomBar && !showSettingsMenu) {
                TopAppBar(
                    title = { },
                    actions = {
                        IconButton(onClick = { showSettingsMenu = true }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.configuracion),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    windowInsets = TopAppBarDefaults.windowInsets
                )
            }
        },
        bottomBar = {
            if (mostrarBottomBar && !showSettingsMenu) {
                NavigationBar(
                    containerColor = Color(0xFF020B18),
                    contentColor = Color.White,
                    tonalElevation = 12.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.inicio)) },
                        label = { Text(stringResource(R.string.inicio), fontSize = 10.sp) },
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
                        icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.mis_datos)) },
                        label = { Text(stringResource(R.string.mis_datos), fontSize = 10.sp) },
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
                        icon = { Icon(Icons.Default.Info, contentDescription = stringResource(R.string.informacion)) },
                        label = { Text(stringResource(R.string.informacion), fontSize = 10.sp) },
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
                        icon = { Icon(Icons.Default.Help, contentDescription = stringResource(R.string.ayuda)) },
                        label = { Text(stringResource(R.string.ayuda), fontSize = 10.sp) },
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

        if (showSettingsMenu) {
            SlideConfiguracion(
                onNavigateBack = { showSettingsMenu = false },
                onLogout = onLogout
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = RutasNavegacion.BIENVENIDA,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                composable(RutasNavegacion.BIENVENIDA) {
                    SlideBienvenida(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding()),
                        onContinuar = {
                            navController.navigate(RutasNavegacion.INGRESO_DNI)
                        }
                    )
                }

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
                                navController.navigate(RutasNavegacion.seleccionLocalidad(dni))
                            }
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

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
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
                                    onDescargarConstancia = {}
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
                                        text = stringResource(R.string.error),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(currentState.mensajeResId),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                composable("informacion") {
                    SlideInformacion(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    )
                }

                composable("ayuda") {
                    SlideAyuda(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    )
                }
            }
        }
    }
}