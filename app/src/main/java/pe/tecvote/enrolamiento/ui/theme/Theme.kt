package pe.tecvote.enrolamiento.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// NO definimos colores aquí, los usamos de Color.kt

private val DarkColorScheme = darkColorScheme(
    primary = CyanBrillante,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = CyanOscuro,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = androidx.compose.ui.graphics.Color(0xFF66E0FF),
    onSecondary = AzulProfundo,
    background = AzulProfundo,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFF0D1B2A),
    onSurface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = AzulOscuro,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
    error = androidx.compose.ui.graphics.Color(0xFFFF4444),
    onError = androidx.compose.ui.graphics.Color.White,
    outline = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f),
    outlineVariant = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.12f)
)

private fun crearTipografia(escala: Float): Typography {
    return Typography(
        headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = (32 * escala).sp, lineHeight = (40 * escala).sp),
        headlineMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = (28 * escala).sp, lineHeight = (36 * escala).sp),
        headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = (24 * escala).sp, lineHeight = (32 * escala).sp),
        titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = (22 * escala).sp, lineHeight = (28 * escala).sp),
        titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = (18 * escala).sp, lineHeight = (24 * escala).sp),
        titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = (16 * escala).sp, lineHeight = (22 * escala).sp),
        bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = (16 * escala).sp, lineHeight = (24 * escala).sp),
        bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = (14 * escala).sp, lineHeight = (20 * escala).sp),
        bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = (12 * escala).sp, lineHeight = (16 * escala).sp),
        labelLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = (15 * escala).sp, lineHeight = (20 * escala).sp, letterSpacing = 2.sp),
        labelMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = (12 * escala).sp, lineHeight = (16 * escala).sp, letterSpacing = 1.sp),
        labelSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = (10 * escala).sp, lineHeight = (14 * escala).sp)
    )
}

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun TecVoteTheme(
    fontSizeScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = crearTipografia(fontSizeScale),
        shapes = Formas,
        content = content
    )
}