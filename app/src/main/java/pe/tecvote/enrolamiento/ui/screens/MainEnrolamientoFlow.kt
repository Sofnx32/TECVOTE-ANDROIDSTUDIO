package pe.tecvote.enrolamiento.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * Contenedor principal de navegación de la aplicación.
 *
 * FLUJO CORRECTO:
 * 1. Bienvenida (pantalla inicial)
 * 2. Ingreso DNI
 * 3. Preguntas de seguridad
 * 4. Biometría (foto facial)
 * 5. Selección de Localidad (NUEVA - elegir donde votar)
 * 6. Gestión de Enrolamiento (revisar y guardar)
 * 7. Mis Datos Completo (ver información final)
 */
@Composable
fun MainEnrolamientoFlow(navController: NavHostController = rememberNavController()) {

    // Estado compartido entre pantallas
    var dniActual by remember { mutableStateOf("") }
    var tokenObtenido by remember { mutableStateOf<String?>(null) }

    // ✅ BackHandler global para el botón atrás
    BackHandler(enabled = navController.previousBackStackEntry != null) {
        navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = RutasNavegacion.BIENVENIDA  // ← VUELVE A BIENVENIDA COMO INICIO
    ) {
        // ── PANTALLA 1: Bienvenida ───────────────────────────────
        composable(RutasNavegacion.BIENVENIDA) {
            SlideBienvenida(
                onContinuar = {
                    navController.navigate(RutasNavegacion.INGRESO_DNI)
                }
            )
        }

        // ── PANTALLA 2: Ingreso DNI ───────────────────────────────
        composable(RutasNavegacion.INGRESO_DNI) {
            SlideIngresoDNI(
                onContinuar = { dniIngresado ->
                    dniActual = dniIngresado
                    navController.navigate(RutasNavegacion.preguntas(dniIngresado))
                }
            )
        }

        // ── PANTALLA 3: Preguntas de seguridad ────────────────────
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

        // ── PANTALLA 4: Biometría ─────────────────────────────────
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
                    // Después de biometría → Selección de Localidad
                    navController.navigate(RutasNavegacion.seleccionLocalidad(dni))
                }
            )
        }

        // ── PANTALLA 5: Selección de Localidad (NUEVA) ────────────
        composable(
            route = RutasNavegacion.SELECCION_LOCALIDAD,
            arguments = listOf(navArgument("dni") { type = NavType.StringType })
        ) { backStackEntry ->
            val dni = backStackEntry.arguments?.getString("dni") ?: ""
            SlideSeleccionLocalidad(
                dni = dni,
                onContinuar = {
                    // Después de seleccionar localidad → Gestión de Enrolamiento
                    navController.navigate(RutasNavegacion.gestionEnrolamiento(dni))
                }
            )
        }

        // ── PANTALLA 6: Gestión de Enrolamiento ──────────────────
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

        // ── PANTALLA 7: Mis Datos Completo ────────────────────────
        composable(
            route = RutasNavegacion.MIS_DATOS,
            arguments = listOf(navArgument("dni") { type = NavType.StringType })
        ) { backStackEntry ->
            val dni = backStackEntry.arguments?.getString("dni") ?: ""
            SlideMisDatosCompleto(
                dni = dni,
                onMostrarQR = {
                    // TODO: Mostrar QR en pantalla completa
                },
                onDescargarConstancia = {
                    // TODO: Descargar constancia en PDF
                }
            )
        }
    }
}