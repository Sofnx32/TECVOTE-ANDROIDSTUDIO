package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.tecvote.enrolamiento.R

@Composable
fun SlideInformacion(modifier: Modifier = Modifier) {
    val azulProfundo = Color(0xFF020B18)
    val azulOscuro = Color(0xFF041529)
    val azulMedio = Color(0xFF0A2547)
    val cyanBrillante = Color(0xFF00C8FF)

    val degradeFondo = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to azulMedio,
            0.4f to azulOscuro,
            1.0f to azulProfundo
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            stringResource(R.string.info_sistema),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            stringResource(R.string.info_subtitulo),
            color = Color.White.copy(0.6f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        InformacionCard(
            icono = "📱",
            titulo = stringResource(R.string.info_version),
            contenido = "TecVote v2.0.1",
            cyanBrillante = cyanBrillante
        )

        Spacer(Modifier.height(12.dp))

        InformacionCard(
            icono = "🏛️",
            titulo = stringResource(R.string.info_desarrollado),
            contenido = stringResource(R.string.info_desarrollado_contenido),
            cyanBrillante = cyanBrillante
        )

        Spacer(Modifier.height(12.dp))

        InformacionCard(
            icono = "📅",
            titulo = stringResource(R.string.info_anio),
            contenido = "2026",
            cyanBrillante = cyanBrillante
        )

        Spacer(Modifier.height(12.dp))

        InformacionCard(
            icono = "🎯",
            titulo = stringResource(R.string.info_proposito),
            contenido = stringResource(R.string.info_proposito_contenido),
            cyanBrillante = cyanBrillante
        )

        Spacer(Modifier.height(12.dp))

        InformacionCard(
            icono = "🔒",
            titulo = stringResource(R.string.info_seguridad),
            contenido = stringResource(R.string.info_seguridad_contenido),
            cyanBrillante = cyanBrillante
        )

        Spacer(Modifier.height(12.dp))

        InformacionCard(
            icono = "📜",
            titulo = stringResource(R.string.info_marco_legal),
            contenido = stringResource(R.string.info_marco_legal_contenido),
            cyanBrillante = cyanBrillante
        )

        Spacer(Modifier.height(12.dp))

        InformacionCard(
            icono = "🌐",
            titulo = stringResource(R.string.info_cobertura),
            contenido = stringResource(R.string.info_cobertura_contenido),
            cyanBrillante = cyanBrillante
        )

        Spacer(Modifier.height(32.dp))

        Text(
            stringResource(R.string.info_copyright),
            color = Color.White.copy(0.4f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun InformacionCard(
    icono: String,
    titulo: String,
    contenido: String,
    cyanBrillante: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                icono,
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titulo,
                    color = cyanBrillante,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    contenido,
                    color = Color.White.copy(0.85f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}