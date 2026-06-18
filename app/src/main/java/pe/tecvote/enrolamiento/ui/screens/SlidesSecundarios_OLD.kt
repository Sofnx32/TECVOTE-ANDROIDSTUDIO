package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun SlideValidacionBiometrica(modifier: Modifier = Modifier) {
    SlideBase(
        modifier = modifier,
        fondo = Brush.verticalGradient(listOf(Color(0xFF283593), Color(0xFF1A237E))),
        emoji = "🤳",
        titulo = "Validación Biométrica",
        subtitulo = "Tomate una selfie para verificar tu identidad.",
        numeroPagina = 3
    )
}

@Composable
fun SlideMisDatos(modifier: Modifier = Modifier) {
    SlideBase(
        modifier = modifier,
        fondo = Brush.verticalGradient(listOf(Color(0xFF00695C), Color(0xFF004D40))),
        emoji = "📊",
        titulo = "Mis Datos Actuales",
        subtitulo = "Revisa la información que tenemos registrada.",
        numeroPagina = 4
    )
}

@Composable
fun SlideGuardarEnviar(modifier: Modifier = Modifier) {
    SlideBase(
        modifier = modifier,
        fondo = Brush.verticalGradient(listOf(Color(0xFF4527A0), Color(0xFF311B92))),
        emoji = "✅",
        titulo = "Confirmar y Enviar",
        subtitulo = "Revisa todo y envía tu actualización.",
        numeroPagina = 5
    )
}