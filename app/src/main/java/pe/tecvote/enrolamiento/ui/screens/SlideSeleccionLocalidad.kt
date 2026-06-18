package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import pe.tecvote.enrolamiento.ui.Espaciados
import pe.tecvote.enrolamiento.ui.EspacioMedio
import pe.tecvote.enrolamiento.ui.EspacioGrande
import pe.tecvote.enrolamiento.ui.EspacioExtraGrande
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos

@Composable
fun SlideSeleccionLocalidad(
    modifier: Modifier = Modifier,
    dni: String = "",  // ← AGREGAR ESTE PARÁMETRO
    onContinuar: () -> Unit = {}
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

            // Título principal
            Text(
                "SELECCIONA TU LOCALIDAD",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            EspacioMedio()

            Text(
                "Usaremos tu ubicación para mostrar tu\ndepartamento y localidades disponibles.",
                color = Color.White.copy(0.7f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            EspacioGrande()

            // Mapa ilustrativo (placeholder)
            Box(
                modifier = Modifier
                    .size(TamanosAdaptativos.tamanoLogoPrincipal())
                    .background(
                        Brush.radialGradient(
                            listOf(
                                cyanBrillante.copy(0.2f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("📍", fontSize = 64.sp)
            }

            EspacioExtraGrande()

            // Card de permiso de ubicación
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContinuar() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(0.08f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(cyanBrillante.copy(0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📍", fontSize = 24.sp)
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Permitir acceso a tu ubicación",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Así detectaremos tu departamento actual\ny te mostraremos las localidades disponibles\npara que elijas la correcta.",
                            color = Color.White.copy(0.6f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Siguiente",
                        tint = cyanBrillante
                    )
                }
            }

            EspacioExtraGrande()

            // Sección: ¿Por qué usamos tu ubicación?
            Text(
                "¿POR QUÉ USAMOS TU UBICACIÓN?",
                color = cyanBrillante,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            EspacioMedio()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ItemRazonUbicacion("🎯", "Te mostramos solo las localidades de tu departamento.", cyanBrillante)
                ItemRazonUbicacion("🔒", "No almacenamos tu ubicación exacta ni compartimos tus datos.", Color(0xFF4CAF50))
                ItemRazonUbicacion("🛡️", "Tu privacidad y seguridad son nuestra prioridad.", Color(0xFF9C27B0))
            }

            Spacer(Modifier.weight(1f))

            // Botones de acción
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyanBrillante
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "USAR MI UBICACIÓN ACTUAL",
                            color = azulProfundo,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Requiere permiso de ubicación",
                            color = azulProfundo.copy(0.7f),
                            fontSize = 10.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = { onContinuar() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, cyanBrillante.copy(0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = cyanBrillante
                    )
                ) {
                    Text(
                        "SELECCIONAR MANUALMENTE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                EspacioMedio()

                Text(
                    "¿Tienes problemas con el permiso? Ver ayuda",
                    color = Color.White.copy(0.4f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(8.dp)
                )
            }

            EspacioExtraGrande()
        }
    }
}

@Composable
private fun ItemRazonUbicacion(icono: String, texto: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color.copy(0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icono, fontSize = 16.sp)
        }

        Spacer(Modifier.width(12.dp))

        Text(
            texto,
            color = Color.White.copy(0.7f),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}