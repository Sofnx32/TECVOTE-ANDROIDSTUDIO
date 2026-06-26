package pe.tecvote.enrolamiento.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


object Espaciados {
    val xs = 4.dp    // Espaciado muy pequeño (entre iconos y texto)
    val sm = 8.dp    // Espaciado pequeño (entre elementos relacionados)
    val md = 16.dp   // Espaciado medio (entre secciones)
    val lg = 24.dp   // Espaciado grande (entre bloques principales)
    val xl = 32.dp   // Espaciado extra grande (márgenes de pantalla)
    val xxl = 48.dp  // Espaciado enorme (separación de secciones grandes)
}


object TamanosAdaptativos {


    @Composable
    fun anchoPantalla(): Dp {
        return LocalConfiguration.current.screenWidthDp.dp
    }


    @Composable
    fun altoPantalla(): Dp {
        return LocalConfiguration.current.screenHeightDp.dp
    }


    @Composable
    fun anchoProporcional(porcentaje: Double): Dp {
        return (anchoPantalla().value * porcentaje).dp
    }


    @Composable
    fun altoProporcional(porcentaje: Double): Dp {
        return (altoPantalla().value * porcentaje).dp
    }

    /**
     * Tamaño de logo/imagen principal adaptativo.
     * Usa el 55% del ancho de la pantalla (máximo 220dp, mínimo 150dp).
     */
    @Composable
    fun tamanoLogoPrincipal(): Dp {
        val calculado = anchoProporcional(0.55)
        return when {
            calculado.value < 150 -> 150.dp
            calculado.value > 220 -> 220.dp
            else -> calculado
        }
    }

    /**
     * Tamaño de imagen secundaria adaptativo.
     * Usa el 30% del ancho de la pantalla (máximo 120dp, mínimo 80dp).
     */
    @Composable
    fun tamanoImagenSecundaria(): Dp {
        val calculado = anchoProporcional(0.30)
        return when {
            calculado.value < 80 -> 80.dp
            calculado.value > 120 -> 120.dp
            else -> calculado
        }
    }

    /**
     * Tamaño de icono/emoji en círculo adaptativo.
     * Usa el 22% del ancho de la pantalla (máximo 110dp, mínimo 70dp).
     */
    @Composable
    fun tamanoIconoCircular(): Dp {
        val calculado = anchoProporcional(0.22)
        return when {
            calculado.value < 70 -> 70.dp
            calculado.value > 110 -> 110.dp
            else -> calculado
        }
    }

    /**
     * Padding horizontal de la pantalla adaptativo.
     * En pantallas pequeñas: 16dp, en grandes: 32dp.
     */
    @Composable
    fun paddingHorizontalPantalla(): Dp {
        return when {
            anchoPantalla().value < 400 -> 16.dp  // Celulares pequeños
            anchoPantalla().value < 600 -> 24.dp  // Celulares grandes / tablets pequeñas
            else -> 32.dp                         // Tablets grandes
        }
    }
}