package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import pe.tecvote.enrolamiento.ui.EspacioMedio
import pe.tecvote.enrolamiento.ui.EspacioGrande
import pe.tecvote.enrolamiento.ui.EspacioExtraGrande
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos
import pe.tecvote.enrolamiento.ui.screens.localidad.LocalidadState
import pe.tecvote.enrolamiento.ui.screens.localidad.LocalidadViewModel

@Composable
fun SlideSeleccionLocalidad(
    modifier: Modifier = Modifier,
    dni: String = "",
    onContinuar: () -> Unit = {},
    viewModel: LocalidadViewModel = viewModel()
) {
    // Paleta de colores
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

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing),
        label = "alpha"
    )

    // Cargar localidad automáticamente al entrar
    LaunchedEffect(dni) {
        if (dni.isNotBlank()) {
            viewModel.cargarLocalidad(dni)
        }
    }

    val state by viewModel.state.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = TamanosAdaptativos.paddingHorizontalPantalla())
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header ONPE
            EspacioExtraGrande()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.06f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ONPE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("OFICINA NACIONAL DE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("PROCESOS ELECTORALES", color = Color.White.copy(0.8f), fontSize = 10.sp)
                }
            }

            EspacioExtraGrande()

            // Contenido dinámico según estado
            when (val currentState = state) {
                is LocalidadState.Cargando -> {
                    ContenidoCargando(cyanBrillante)
                }
                is LocalidadState.LocalDetectado -> {
                    ContenidoLocalDetectado(
                        state = currentState,
                        cyanBrillante = cyanBrillante,
                        azulProfundo = azulProfundo,
                        onContinuar = onContinuar
                    )
                }
                is LocalidadState.PendienteAsignacion -> {
                    ContenidoPendiente(
                        mensaje = currentState.mensaje,
                        cyanBrillante = cyanBrillante
                    )
                }
                is LocalidadState.Error -> {
                    ContenidoError(
                        mensaje = currentState.mensaje,
                        cyanBrillante = cyanBrillante,
                        onReintentar = { viewModel.cargarLocalidad(dni) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
            EspacioExtraGrande()
        }
    }
}

@Composable
private fun ContenidoCargando(color: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = color,
            modifier = Modifier.size(64.dp)
        )
        EspacioGrande()
        Text(
            "Buscando tu local de votación...",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ContenidoLocalDetectado(
    state: LocalidadState.LocalDetectado,
    cyanBrillante: Color,
    azulProfundo: Color,
    onContinuar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            "TU LOCAL DE VOTACIÓN",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        EspacioMedio()

        Text(
            "Hemos encontrado tu local asignado",
            color = Color.White.copy(0.7f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        EspacioGrande()

        // Mapa de Google
        val ubicacionLocal = LatLng(state.latitud, state.longitud)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(ubicacionLocal, 16f)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = false
                )
            ) {
                Marker(
                    state = MarkerState(position = ubicacionLocal),
                    title = state.nombreLocal,
                    snippet = state.direccion
                )
            }
        }

        EspacioGrande()

        // Card con información del local
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(0.08f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = cyanBrillante,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        state.nombreLocal,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    state.direccion,
                    color = Color.White.copy(0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                if (state.distrito.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.distrito,
                        color = cyanBrillante,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        EspacioGrande()

        // Botón continuar
        Button(
            onClick = onContinuar,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = cyanBrillante
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = azulProfundo,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "VER MI LOCALIDAD",
                    color = azulProfundo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ContenidoPendiente(mensaje: String, cyanBrillante: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            tint = cyanBrillante,
            modifier = Modifier.size(80.dp)
        )
        EspacioGrande()
        Text(
            "LOCAL PENDIENTE",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        EspacioMedio()
        Text(
            mensaje,
            color = Color.White.copy(0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ContenidoError(
    mensaje: String,
    cyanBrillante: Color,
    onReintentar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFFF5252),
            modifier = Modifier.size(80.dp)
        )
        EspacioGrande()
        Text(
            "ERROR",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        EspacioMedio()
        Text(
            mensaje,
            color = Color.White.copy(0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        EspacioGrande()
        OutlinedButton(
            onClick = onReintentar,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = cyanBrillante
            )
        ) {
            Text(
                "REINTENTAR",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}