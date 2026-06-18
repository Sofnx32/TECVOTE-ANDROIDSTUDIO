package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import pe.tecvote.enrolamiento.ui.*

@Composable
fun SlideMisDatosCompleto(
    modifier: Modifier = Modifier,
    dni: String = "",
    onMostrarQR: () -> Unit = {},
    onDescargarConstancia: () -> Unit = {}
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

    var tabSeleccionado by remember { mutableStateOf(0) }

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
            // Header
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
                    Text("OFICINA NACIONAL DE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("PROCESOS ELECTORALES", color = Color.White.copy(0.8f), fontSize = 10.sp)
                }
            }

            EspacioGrande()

            // Bienvenida + Badge de estado
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
                    Text(
                        "¡Bienvenido, Juan Carlos!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Consulta tu información electoral y\ntu mesa de votación.",
                        color = Color.White.copy(0.6f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Badge de estado
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = verdeExito.copy(0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                "ENROLADO",
                                color = verdeExito,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Registro verificado",
                                color = verdeExito.copy(0.7f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            EspacioGrande()

            // Contenido scrollable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card: Mis Datos
                CardMisDatos(cyanBrillante, verdeExito)  // ← CORREGIDO: Se agregó verdeExito

                // Card: Mesa de Votación
                CardMesaVotacion(cyanBrillante)

                // Card: Miembro de Mesa
                CardMiembroMesa(verdeExito, cyanBrillante)
            }

            EspacioGrande()

            // Botones de acción
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onMostrarQR() },
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
                        Text("📱", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                "MOSTRAR MI CÓDIGO QR",
                                color = azulProfundo,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Para verificación en local de votación",
                                color = azulProfundo.copy(0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = { onDescargarConstancia() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, cyanBrillante.copy(0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = cyanBrillante
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("️", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "DESCARGAR MI CONSTANCIA DE SUFRAGIO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            EspacioMedio()

            // Bottom Navigation
            BottomNavegacion(tabSeleccionado = tabSeleccionado, onTabSeleccionado = { tabSeleccionado = it })
        }
    }
}

@Composable
private fun CardMisDatos(cyanBrillante: Color, verdeExito: Color) {  // ← CORREGIDO: Se agregó verdeExito
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "MIS DATOS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            EspacioMedio()

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Columna izquierda: Datos
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilaDatoPersonal("👤", "Nombres", "JUAN CARLOS", cyanBrillante)
                    FilaDatoPersonal("🆔", "Apellidos", "PÉREZ GÓMEZ", cyanBrillante)
                    FilaDatoPersonal("", "DNI", "12345678", cyanBrillante)
                    FilaDatoPersonal("📅", "Fecha de nacimiento", "15/05/1990", cyanBrillante)
                    FilaDatoPersonal("📍", "Departamento", "LIMA", cyanBrillante)
                    FilaDatoPersonal("🏛️", "Provincia", "LIMA", cyanBrillante)
                    FilaDatoPersonal("📌", "Distrito", "ATE", cyanBrillante)
                    FilaDatoPersonal("🏠", "Dirección", "AV. LOS VIRREYES 1234, ATE, LIMA", cyanBrillante)
                }

                Spacer(Modifier.width(16.dp))

                // Columna derecha: Badge de ciudadano habilitado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(120.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(verdeExito.copy(0.15f), CircleShape)
                            .border(2.dp, verdeExito, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤✓", fontSize = 32.sp)
                    }

                    EspacioPequeno()

                    Text(
                        "Ciudadano\nhabilitado",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    EspacioPequeno()

                    Text(
                        "Usted está habilitado\npara votar.",
                        color = Color.White.copy(0.5f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaDatoPersonal(icono: String, label: String, valor: String, cyan: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icono, fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.White.copy(0.5f), fontSize = 9.sp)
            Text(valor, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 11.sp)
        }
    }
}

@Composable
private fun CardMesaVotacion(cyanBrillante: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "TU MESA DE VOTACIÓN",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            EspacioMedio()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ilustración de urna
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(cyanBrillante.copy(0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗳️", fontSize = 48.sp)
                }

                Spacer(Modifier.width(16.dp))

                // Información de la mesa
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilaInfoMesa("Mesa de votación", "030390", "🪑", cyanBrillante)
                    FilaInfoMesa("Local de votación", "IE. MARISCAL RAMÓN CASTILLA", "🏫", cyanBrillante)
                    FilaInfoMesa("Dirección", "AV. LOS VIRREYES 1234, ATE, LIMA", "📍", cyanBrillante)
                    FilaInfoMesa("Referencia", "Alt. cuadra 12 de Av. Los Virreyes", "🎯", cyanBrillante)
                    FilaInfoMesa("Horario de votación", "7:00 a. m. - 7:00 p. m.", "🕐", cyanBrillante)
                }
            }
        }
    }
}

@Composable
private fun FilaInfoMesa(label: String, valor: String, icono: String, cyan: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icono, fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.White.copy(0.5f), fontSize = 9.sp)
            Text(valor, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 11.sp)
        }
    }
}

@Composable
private fun CardMiembroMesa(verdeExito: Color, cyanBrillante: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(verdeExito.copy(0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👥", fontSize = 24.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "¿Eres miembro de mesa?",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
                Text(
                    "Consulta si fuiste seleccionado como\nmiembro de mesa.",
                    color = Color.White.copy(0.5f),
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                "No eres miembro\nde mesa",
                color = verdeExito,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End
            )

            Spacer(Modifier.width(8.dp))

            Text("›", color = cyanBrillante, fontSize = 20.sp)
        }
    }
}

@Composable
private fun BottomNavegacion(tabSeleccionado: Int, onTabSeleccionado: (Int) -> Unit) {
    val cyanBrillante = Color(0xFF00C8FF)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color.White.copy(0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabBottomNav(
                icono = "🏠",
                label = "Inicio",
                seleccionado = tabSeleccionado == 0,
                color = cyanBrillante
            ) { onTabSeleccionado(0) }

            TabBottomNav(
                icono = "👤",
                label = "Mis datos",
                seleccionado = tabSeleccionado == 1,
                color = cyanBrillante
            ) { onTabSeleccionado(1) }

            TabBottomNav(
                icono = "ℹ️",
                label = "Información",
                seleccionado = tabSeleccionado == 2,
                color = cyanBrillante
            ) { onTabSeleccionado(2) }

            TabBottomNav(
                icono = "❓",
                label = "Ayuda",
                seleccionado = tabSeleccionado == 3,
                color = cyanBrillante
            ) { onTabSeleccionado(3) }
        }
    }
}

@Composable
private fun TabBottomNav(icono: String, label: String, seleccionado: Boolean, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            icono,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color = if (seleccionado) color else Color.White.copy(0.5f),
            fontSize = 10.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
        )
    }
}