package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SlideBase(
    modifier        : Modifier = Modifier,
    fondo           : Brush,
    emoji           : String,
    titulo          : String,
    subtitulo       : String,
    numeroPagina    : Int,
    totalPaginas    : Int = 6,
    contenidoExtra  : @Composable ColumnScope.() -> Unit = {}
) {
    Box(
        modifier         = modifier.fillMaxSize().background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(shape = CircleShape, color = Color.White.copy(0.15f), modifier = Modifier.size(96.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 38.sp)
                }
            }
            Spacer(Modifier.height(28.dp))
            Text(titulo, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(subtitulo, color = Color.White.copy(0.8f), fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 22.sp)

            contenidoExtra()

            Spacer(Modifier.height(36.dp))
            PuntosIndicadores(total = totalPaginas, actual = numeroPagina - 1)
        }
    }
}

@Composable
fun PuntosIndicadores(total: Int, actual: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == actual) 22.dp else 8.dp, 8.dp)
                    .background(
                        color = if (i == actual) Color.White else Color.White.copy(0.3f),
                        shape = CircleShape
                    )
            )
        }
    }
}