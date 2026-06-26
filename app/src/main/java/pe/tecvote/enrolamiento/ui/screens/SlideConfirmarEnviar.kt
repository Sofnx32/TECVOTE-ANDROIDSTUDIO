package pe.tecvote.enrolamiento.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.tecvote.enrolamiento.data.RespuestaGuardar
import pe.tecvote.enrolamiento.ui.Espaciados
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos

@Composable
fun SlideConfirmarEnviar(
    modifier: Modifier = Modifier,
    dni: String,
    enviando: Boolean = false,                // ← Controlado externamente
    errorGuardar: String? = null,             // ← Controlado externamente
    resultadoGuardar: RespuestaGuardar? = null, // ← Controlado externamente
    onConfirmarEnrolamiento: () -> Unit = {},  // ← Callback para que el VM ejecute la petición
    onFinalizar: () -> Unit = {}
) {
    // Determinamos el éxito según la respuesta que nos pase el ViewModel
    val exito = resultadoGuardar?.exitoso == true
    val codigoConstancia = resultadoGuardar?.codigo_constancia ?: ""

    SlideBase(
        modifier     = modifier,
        fondo        = Brush.verticalGradient(listOf(Color(0xFF4527A0), Color(0xFF1A0050))),
        emoji        = "✅",
        titulo       = "Confirmar y enviar",
        subtitulo    = "Revisa todo antes de finalizar.",
        numeroPagina = 6,
        totalPaginas = 6
    ) {
        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(!exito) {
            Column {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White.copy(0.12f))
                ) {
                    Column(Modifier.padding(Espaciados.lg) ) {
                        Text(
                            "Resumen del enrolamiento",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        FilaResumen("✅", "Identidad verificada por DNI")
                        FilaResumen("✅", "Preguntas de seguridad correctas")
                        FilaResumen("✅", "Biometría facial validada")
                        FilaResumen("✅", "Datos electorales revisados")
                    }
                }

                Spacer(Modifier.height(16.dp))

                Surface(
                    shape  = RoundedCornerShape(10.dp),
                    color  = Color.White.copy(0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Al confirmar declaras que los datos proporcionados son correctos y tienen carácter de declaración jurada.",
                        color     = Color.White.copy(0.75f),
                        fontSize  = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier  = Modifier.padding(16.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                if (errorGuardar != null) {
                    Text(errorGuardar, color = Color(0xFFFF6B6B), fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick  = { onConfirmarEnrolamiento() }, // ← Delegación limpia al ViewModel
                    enabled  = !enviando,
                    shape    = RoundedCornerShape(28.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF4527A0)),
                    modifier = Modifier.fillMaxWidth().height(TamanosAdaptativos.altoProporcional(0.07))
                ) {
                    if (enviando)
                        CircularProgressIndicator(color = Color(0xFF4527A0), modifier = Modifier.size(24.dp))
                    else
                        Text("Confirmar enrolamiento", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }

        AnimatedVisibility(
            visible = exito,
            enter   = fadeIn() + expandVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉", fontSize = 56.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "¡Enrolamiento completado!",
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 22.sp,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tu registro ha sido procesado exitosamente.",
                    color    = Color.White.copy(0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier            = Modifier.padding(Espaciados.xl) ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Código de constancia", color = Color.Gray, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            codigoConstancia,
                            color      = Color(0xFF4527A0),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 28.sp,
                            letterSpacing = 4.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Guarda este código. Lo necesitarás si realizas consultas sobre tu enrolamiento.",
                            color     = Color.DarkGray,
                            fontSize  = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onFinalizar,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4527A0)
                    ),
                    modifier = Modifier.fillMaxWidth().height(TamanosAdaptativos.altoProporcional(0.07))
                ) {
                    Text("🏠 VOLVER AL INICIO", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun FilaResumen(icono: String, texto: String) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icono, fontSize = 16.sp)
        Spacer(Modifier.width(10.dp))
        Text(texto, color = Color.White.copy(0.9f), fontSize = 14.sp)
    }
}