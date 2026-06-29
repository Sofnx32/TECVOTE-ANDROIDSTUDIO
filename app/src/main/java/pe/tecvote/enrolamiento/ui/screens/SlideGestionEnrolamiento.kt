package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.data.RespuestaElector
import pe.tecvote.enrolamiento.ui.*

@Composable
fun SlideGestionEnrolamiento(
    modifier: Modifier = Modifier,
    dni: String = "",
    datosElector: RespuestaElector? = null,
    onContinuar: () -> Unit = {},
    onCancelar: () -> Unit = {}
) {
    val azulProfundo = Color(0xFF020B18)
    val azulOscuro = Color(0xFF041529)
    val azulMedio = Color(0xFF0A2547)
    val cyanBrillante = Color(0xFF00C8FF)
    val verdeExito = Color(0xFF4CAF50)

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

    if (datosElector == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(degradeFondo),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = cyanBrillante,
                    strokeWidth = 3.dp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.cargando_informacion),
                    color = Color.White.copy(0.7f),
                    fontSize = 14.sp
                )
            }
        }
        return
    }

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
                .alpha(alpha)
        ) {
            EspacioMedio()
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
                    Text(stringResource(R.string.oficina_nacional), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.procesos_electorales), color = Color.White.copy(0.8f), fontSize = 10.sp)
                }
            }

            EspacioGrande()

            Text(
                stringResource(R.string.gestion_enrolamiento),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            EspacioPequeno()

            Text(
                stringResource(R.string.verifique_informacion),
                color = Color.White.copy(0.7f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 20.sp
            )

            EspacioGrande()
            StepperEnrolamiento(pasoActual = 2)
            EspacioGrande()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardInformacionCiudadano(
                    cyanBrillante = cyanBrillante,
                    dni = dni,
                    nombreCompleto = datosElector.nombre ?: stringResource(R.string.sin_nombre),
                    ubigeo = datosElector.ubigeo_legal ?: "------"
                )

                CardDatosEnrolados(cyanBrillante, verdeExito)
                CardSincronizacion(cyanBrillante)
            }

            EspacioGrande()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onContinuar() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cyanBrillante)
                ) {
                    Text(
                        stringResource(R.string.guardar_enrolamiento),
                        color = azulProfundo,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }

                TextButton(
                    onClick = { onCancelar() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.cancelar),
                        color = cyanBrillante.copy(0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("❓", fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.problemas_guardar), color = Color.White.copy(0.4f), fontSize = 11.sp)
                    Text(
                        stringResource(R.string.ver_ayuda),
                        color = cyanBrillante,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { }
                    )
                }
            }
            EspacioMedio()
        }
    }
}

@Composable
private fun CardInformacionCiudadano(
    cyanBrillante: Color,
    dni: String,
    nombreCompleto: String,
    ubigeo: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.informacion_ciudadano),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            EspacioMedio()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(cyanBrillante.copy(0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 32.sp)
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(stringResource(R.string.dni), color = Color.White.copy(0.5f), fontSize = 9.sp)
                            Text(dni, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text(stringResource(R.string.apellidos_nombres), color = Color.White.copy(0.5f), fontSize = 9.sp)
                            Text(nombreCompleto.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    EspacioMedio()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(stringResource(R.string.ubigeo_legal), color = Color.White.copy(0.5f), fontSize = 9.sp)
                            Text(ubigeo, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepperEnrolamiento(pasoActual: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperItem(paso = 1, estado = if (pasoActual > 1) 1 else if (pasoActual == 1) 0 else -1, label = stringResource(R.string.verificacion), sublabel = stringResource(R.string.completada))
        Spacer(modifier = Modifier.width(8.dp))
        StepperItem(paso = 2, estado = if (pasoActual > 2) 1 else if (pasoActual == 2) 0 else -1, label = stringResource(R.string.guardar), sublabel = stringResource(R.string.enrolamiento), esActivo = true)
        Spacer(modifier = Modifier.width(8.dp))
        StepperItem(paso = 3, estado = if (pasoActual > 3) 1 else if (pasoActual == 3) 0 else -1, label = stringResource(R.string.confirmacion), sublabel = stringResource(R.string.pendiente))
    }
}

@Composable
private fun StepperItem(paso: Int, estado: Int, label: String, sublabel: String = "", esActivo: Boolean = false) {
    val cyanBrillante = Color(0xFF00C8FF)
    val verdeExito = Color(0xFF4CAF50)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (estado == 1) verdeExito.copy(0.2f) else if (estado == 0) cyanBrillante.copy(0.2f) else Color.White.copy(0.05f), CircleShape)
                .border(if (estado >= 0) 2.dp else 1.dp, if (estado == 1) verdeExito else if (estado == 0) cyanBrillante else Color.White.copy(0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (estado == 1) Text("✓", color = verdeExito, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            else Text("$paso", color = if (estado == 0) cyanBrillante else Color.White.copy(0.5f), fontSize = if (estado == 0) 18.sp else 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = if (estado == 1) verdeExito else if (estado == 0) cyanBrillante else Color.White.copy(0.7f), fontSize = 11.sp, fontWeight = if (esActivo) FontWeight.Bold else FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CardDatosEnrolados(cyanBrillante: Color, verdeExito: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.datos_enrolados), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            EspacioMedio()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FilaDatoEnrolado("📷", stringResource(R.string.rostro_biometrico), stringResource(R.string.enrolado), verdeExito)
                FilaDatoEnrolado("☁️", stringResource(R.string.firma_digital), stringResource(R.string.vinculada), verdeExito)
            }
        }
    }
}

@Composable
private fun FilaDatoEnrolado(icono: String, label: String, estado: String, colorExito: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icono, fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))
            Text(label, color = Color.White.copy(0.8f), fontSize = 13.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(estado, color = colorExito, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(20.dp).background(colorExito.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Text("✓", color = colorExito, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CardSincronizacion(cyanBrillante: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(cyanBrillante.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Text("☁️", fontSize = 24.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.sincronizacion), color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                Text(stringResource(R.string.datos_guardaran), color = Color.White.copy(0.5f), fontSize = 10.sp)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = cyanBrillante.copy(0.2f)) {
                Text(stringResource(R.string.listo), color = cyanBrillante, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}