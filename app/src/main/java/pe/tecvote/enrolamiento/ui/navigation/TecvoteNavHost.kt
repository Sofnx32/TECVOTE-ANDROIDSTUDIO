package pe.tecvote.enrolamiento.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pe.tecvote.enrolamiento.ui.screens.RutasNavegacion
import pe.tecvote.enrolamiento.ui.screens.SlideIngresoDNI
import pe.tecvote.enrolamiento.ui.screens.SlidePreguntas
import pe.tecvote.enrolamiento.ui.screens.SlideSeleccionLocalidad
import androidx.compose.runtime.Composable


@Composable
fun TecvoteNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = RutasNavegacion.INGRESO_DNI
    ) {
        composable(route = RutasNavegacion.INGRESO_DNI) {
            SlideIngresoDNI(
                onContinuar = { dniIngresado ->

                    navController.navigate(RutasNavegacion.preguntas(dniIngresado))
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
    }
}