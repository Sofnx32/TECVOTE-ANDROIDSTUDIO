package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pe.tecvote.enrolamiento.data.RespuestaMisDatos
import pe.tecvote.enrolamiento.data.ElectorData
import pe.tecvote.enrolamiento.data.MesaData
import pe.tecvote.enrolamiento.data.LocalVotacionData
import pe.tecvote.enrolamiento.data.MiembroMesaData
import pe.tecvote.enrolamiento.util.ConversionUtil
import pe.tecvote.enrolamiento.ui.*

@Composable
fun SlideMisDatosCompleto(
    modifier: Modifier = Modifier,
    datos: RespuestaMisDatos,
    onDescargarConstancia: (String?) -> Unit = {}
) {
    // Paleta de colores institucionales de alta seguridad
    val azulProfundo = Color(0xFF020B18)
    val azulOscuro = Color(0xFF041529)
    val azulMedio = Color(0xFF0A2547)
    val cyanBrillante = Color(0xFF00C8FF)
    val verdeExito = Color(0xFF2E7D32)
    val superficieGris = Color(0xFF1E293B)

    val degradeFondo = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to azulMedio,
            0.4f to azulOscuro,
            1.0f to azulProfundo
        )
    )

    var visible by remember { mutableStateOf(false) }
    var mostrarDialogoQR by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = LinearOutSlowInEasing),
        label = "fade_in"
    )

    var tabSeleccionado by remember { mutableStateOf(1) }

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
            // Cabecera Oficial del Organismo Electoral
            EspacioMedio()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(0.04f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(0.12f), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ONPE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("OFICINA NACIONAL DE PROCESOS ELECTORALES", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("SISTEMA NACIONAL DE ENROLAMIENTO TECNOLÓGICO", color = Color.White.copy(0.6f), fontSize = 9.sp)
                }
            }

            EspacioGrande()

            // Panel de Identificación y Estado de Enrolamiento Biométrico
            datos.elector?.let { elector ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = elector.nombreCompleto,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Documento Nacional de Identidad: ${elector.dni}",
                            color = Color.White.copy(0.6f),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    if (elector.biometricEnrolled || elector.estadoEnrolamiento == "COMPLETO") {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = verdeExito.copy(0.2f),
                            border = BorderStroke(1.dp, verdeExito)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("VERIFICADO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("BIOMETRÍA OK", color = Color.White.copy(0.7f), fontSize = 8.sp)
                            }
                        }
                    }
                }
            }

            EspacioGrande()

            // Contenedor Principal de Datos
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                datos.elector?.let { CardMisDatos(it, cyanBrillante, verdeExito, superficieGris) }
                CardMesaVotacion(datos.mesa, datos.localVotacion, cyanBrillante, superficieGris)
                datos.miembroMesa?.let { CardMiembroMesa(it, verdeExito, cyanBrillante, superficieGris) }
            }

            EspacioGrande()

            // Panel de Acciones Críticas
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { mostrarDialogoQR = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cyanBrillante)
                ) {
                    Text("MOSTRAR CREDENCIAL DIGITAL (QR)", color = azulProfundo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { onDescargarConstancia(datos.codigoConstancia) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, cyanBrillante.copy(0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = cyanBrillante)
                ) {
                    Text("DESCARGAR CONSTANCIA DE SUFRAGIO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            EspacioMedio()
            BottomNavegacion(tabSeleccionado = tabSeleccionado, onTabSeleccionado = { tabSeleccionado = it })
        }
    }

    // Modal de Seguridad para Despliegue de Código QR
    if (mostrarDialogoQR) {
        Dialog(onDismissRequest = { mostrarDialogoQR = false }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = azulOscuro,
                border = BorderStroke(1.dp, Color.White.copy(0.1f)),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("CREDENCIAL OFICIAL DE VERIFICACIÓN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Código: ${datos.codigoConstancia ?: "N/A"}", color = Color.White.copy(0.5f), fontSize = 11.sp)

                    Spacer(Modifier.height(20.dp))

                    val bitmapQR = remember(datos.qrBase64) {
                        ConversionUtil.decodificarBase64AImageBitmap(datos.qrBase64)
                    }

                    if (bitmapQR != null) {
                        Image(
                            bitmap = bitmapQR,
                            contentDescription = "Código QR del Elector",
                            modifier = Modifier
                                .size(220.dp)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(220.dp).background(Color.White.copy(0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error al procesar firma digital QR", color = Color.Red, fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { mostrarDialogoQR = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("CERRAR", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CardMisDatos(elector: ElectorData, cyanBrillante: Color, verdeExito: Color, fondo: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DATOS FILIATORIOS DEL ELECTOR",
                color = cyanBrillante,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            EspacioMedio()
            FilaDatoPersonal("Apellidos y Nombres", elector.nombreCompleto)
            FilaDatoPersonal("Documento de Identidad", elector.dni)
            FilaDatoPersonal("Condición de Enrolamiento", elector.estadoEnrolamiento)
        }
    }
}

@Composable
private fun CardMesaVotacion(mesa: MesaData?, local: LocalVotacionData?, cyanBrillante: Color, fondo: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ASIGNACIÓN GEOGRÁFICA Y LOGÍSTICA DE SUFRAGIO", color = cyanBrillante, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            EspacioMedio()
            FilaDatoPersonal("Número de Mesa", mesa?.codigo ?: "NO REGISTRADO")
            FilaDatoPersonal("Ubicación en Centro", "Aula: ${mesa?.aula ?: "—"} / Piso: ${mesa?.piso ?: "—"}")
            FilaDatoPersonal("Centro de Votación", local?.nombre ?: "NO ASIGNADO")
            FilaDatoPersonal("Dirección del Local", local?.direccion ?: "NO ASIGNADA")
            FilaDatoPersonal("Ubigeo Geográfico", local?.ubigeo ?: "N/A")
        }
    }
}

@Composable
private fun CardMiembroMesa(miembro: MiembroMesaData, verdeExito: Color, cyanBrillante: Color, fondo: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("CONDICIÓN DE MIEMBRO DE MESA", color = cyanBrillante, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    text = if (miembro.esMiembro) "DESIGNADO" else "NO SELECCIONADO",
                    color = if (miembro.esMiembro) cyanBrillante else Color.White.copy(0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            if (miembro.esMiembro) {
                EspacioMedio()
                FilaDatoPersonal("Cargo Asignado", miembro.cargo)
                FilaDatoPersonal("Horario de Obligación", miembro.horario ?: "—")
            }
        }
    }
}

@Composable
private fun FilaDatoPersonal(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(0.5f), fontSize = 11.sp)
        Text(valor, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, textAlign = TextAlign.End)
    }
}

@Composable
private fun BottomNavegacion(tabSeleccionado: Int, onTabSeleccionado: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        color = Color.White.copy(0.06f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val opciones = listOf("Inicio", "Mis datos", "Información", "Ayuda")
            opciones.forEachIndexed { index, label ->
                Text(
                    text = label,
                    color = if (tabSeleccionado == index) Color(0xFF00C8FF) else Color.White.copy(0.4f),
                    fontSize = 12.sp,
                    fontWeight = if (tabSeleccionado == index) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clickable { onTabSeleccionado(index) }
                        .padding(8.dp)
                )
            }
        }
    }
}