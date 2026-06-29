package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

private data class ItemInformacion(
    val titulo: String,
    val contenido: String
)

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

    // Almacenamos las traducciones en memoria mediante 'remember' para evitar que
    // se vuelvan a consultar los recursos XML del sistema durante el scroll.
    val listaInformacion = remember {
        listOf(
            ItemInformacion("info_version", "TecVote v2.0.1"),
            ItemInformacion("info_desarrollado", "info_desarrollado_contenido"),
            ItemInformacion("info_anio", "2026"),
            ItemInformacion("info_proposito", "info_proposito_contenido"),
            ItemInformacion("info_seguridad", "info_seguridad_contenido"),
            ItemInformacion("info_marco_legal", "info_marco_legal_contenido"),
            ItemInformacion("info_cobertura", "info_cobertura_contenido")
        )
    }

    // Mapa dinámico para transformar los identificadores string estáticos en recursos del proyecto de forma limpia
    val mapeoRecursos = mapOf(
        "info_version" to stringResource(R.string.info_version),
        "info_desarrollado" to stringResource(R.string.info_desarrollado),
        "info_desarrollado_contenido" to stringResource(R.string.info_desarrollado_contenido),
        "info_anio" to stringResource(R.string.info_anio),
        "info_proposito" to stringResource(R.string.info_proposito),
        "info_proposito_contenido" to stringResource(R.string.info_proposito_contenido),
        "info_seguridad" to stringResource(R.string.info_seguridad),
        "info_seguridad_contenido" to stringResource(R.string.info_seguridad_contenido),
        "info_marco_legal" to stringResource(R.string.info_marco_legal),
        "info_marco_legal_contenido" to stringResource(R.string.info_marco_legal_contenido),
        "info_cobertura" to stringResource(R.string.info_cobertura),
        "info_cobertura_contenido" to stringResource(R.string.info_cobertura_contenido)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
            .systemBarsPadding(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.info_sistema),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.info_subtitulo),
                color = Color.White.copy(0.65f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(28.dp)) // Ajuste proporcional de espaciado por el spacedBy del LazyColumn
        }

        // Renderizado eficiente y dinámico por demanda (solo dibuja lo que se ve en pantalla)
        items(listaInformacion) { item ->
            val tituloResuelto = mapeoRecursos[item.titulo] ?: item.titulo
            val contenidoResuelto = mapeoRecursos[item.contenido] ?: item.contenido

            InformacionCard(
                titulo = tituloResuelto,
                contenido = contenidoResuelto,
                cyanBrillante = cyanBrillante
            )
        }

        item {
            Spacer(Modifier.height(36.dp))
            Text(
                text = stringResource(R.string.info_copyright),
                color = Color.White.copy(0.4f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun InformacionCard(
    titulo: String,
    contenido: String,
    cyanBrillante: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = titulo,
                color = cyanBrillante,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = contenido,
                color = Color.White.copy(0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}