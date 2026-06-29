package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.ui.EspacioMedio
import pe.tecvote.enrolamiento.ui.EspacioGrande
import pe.tecvote.enrolamiento.ui.EspacioExtraGrande
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos
import pe.tecvote.enrolamiento.ui.screens.localidad.LocalidadState
import pe.tecvote.enrolamiento.ui.screens.localidad.LocalidadViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@Composable
fun SlideSeleccionLocalidad(
    modifier: Modifier = Modifier,
    dni: String = "",
    onContinuar: () -> Unit = {},
    viewModel: LocalidadViewModel = viewModel()
) {
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
                    Text(
                        stringResource(R.string.oficina_nacional),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.procesos_electorales),
                        color = Color.White.copy(0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            EspacioExtraGrande()

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
                    ContenidoPendiente(cyanBrillante = cyanBrillante)
                }
                is LocalidadState.Error -> {
                    ContenidoError(
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
            stringResource(R.string.buscando_local),
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
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.tu_local_votacion),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        EspacioMedio()

        Text(
            stringResource(R.string.ubicacion_centro),
            color = Color.White.copy(0.7f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        EspacioGrande()

        val ubicacionLocal = LatLng(state.latitud, state.longitud)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(ubicacionLocal, 16f)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.08f))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = cyanBrillante, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(state.nombreLocal, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Text(state.direccion, color = Color.White.copy(0.8f), fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text(state.distrito, color = cyanBrillante, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        EspacioGrande()

        OutlinedButton(
            onClick = {
                val mapaIntentUri = Uri.parse("geo:${state.latitud},${state.longitud}?q=${Uri.encode(state.nombreLocal)}")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapaIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                context.startActivity(mapIntent)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = cyanBrillante)
        ) {
            Text(
                stringResource(R.string.como_llegar),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onContinuar,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = cyanBrillante)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = azulProfundo, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.confirmar_ver_datos),
                    color = azulProfundo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ContenidoPendiente(cyanBrillante: Color) {
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
            stringResource(R.string.local_pendiente),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        EspacioMedio()
        Text(
            stringResource(R.string.revisa_todo),
            color = Color.White.copy(0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ContenidoError(
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
            stringResource(R.string.error),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        EspacioMedio()
        Text(
            stringResource(R.string.sin_conexion_tecvote),
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
                stringResource(R.string.reintentar),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}