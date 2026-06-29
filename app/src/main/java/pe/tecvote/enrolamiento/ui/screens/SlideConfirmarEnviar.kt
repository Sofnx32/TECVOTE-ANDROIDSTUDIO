package pe.tecvote.enrolamiento.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.data.RespuestaGuardar
import pe.tecvote.enrolamiento.ui.Espaciados
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos

@Composable
fun SlideConfirmarEnviar(
    modifier: Modifier = Modifier,
    dni: String,
    enviando: Boolean = false,
    errorGuardar: String? = null,
    resultadoGuardar: RespuestaGuardar? = null,
    onConfirmarEnrolamiento: () -> Unit = {},
    onFinalizar: () -> Unit = {}
) {
    val exito = resultadoGuardar?.exitoso == true
    val codigoConstancia = resultadoGuardar?.codigo_constancia ?: ""

    SlideBase(
        modifier     = modifier,
        fondo        = Brush.verticalGradient(listOf(Color(0xFF4527A0), Color(0xFF1A0050))),
        titulo       = stringResource(R.string.confirmar_enviar),
        subtitulo    = stringResource(R.string.revisa_todo),
        numeroPagina = 6,
        totalPaginas = 6,
        iconoCabecera = {
            // Reemplazo del emoji por un icono vectorial institucional
            Icon(
                painter = painterResource(id = R.drawable.ic_verificacion),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    ) {
        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(!exito) {
            Column {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White.copy(0.12f))
                ) {
                    Column(Modifier.padding(Espaciados.lg)) {
                        Text(
                            text = stringResource(R.string.resumen_enrolamiento),
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        FilaResumen(stringResource(R.string.identidad_verificada_dni))
                        FilaResumen(stringResource(R.string.preguntas_seguridad_correctas))
                        FilaResumen(stringResource(R.string.biometria_facial_validada))
                        FilaResumen(stringResource(R.string.datos_electorales_revisados))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Surface(
                    shape  = RoundedCornerShape(10.dp),
                    color  = Color.White.copy(0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.declaracion_jurada),
                        color     = Color.White.copy(0.75f),
                        fontSize  = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier  = Modifier.padding(16.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                if (errorGuardar != null) {
                    Text(text = errorGuardar, color = Color(0xFFFF6B6B), fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick  = { onConfirmarEnrolamiento() },
                    enabled  = !enviando,
                    shape    = RoundedCornerShape(28.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF4527A0)),
                    modifier = Modifier.fillMaxWidth().height(TamanosAdaptativos.altoProporcional(0.07))
                ) {
                    if (enviando)
                        CircularProgressIndicator(color = Color(0xFF4527A0), modifier = Modifier.size(24.dp))
                    else
                        Text(text = stringResource(R.string.confirmar_enrolamiento), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }

        AnimatedVisibility(
            visible = exito,
            enter   = fadeIn() + expandVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Reemplazo de la cabecera del éxito por un icono circular corporativo verde
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_exito),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.enrolamiento_completado),
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 22.sp,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.registro_procesado),
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
                        modifier            = Modifier.padding(Espaciados.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.codigo_constancia), color = Color.Gray, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = codigoConstancia,
                            color      = Color(0xFF4527A0),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 28.sp,
                            letterSpacing = 4.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.guarda_codigo),
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
                    Text(text = stringResource(R.string.volver_inicio), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun FilaResumen(texto: String) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Marcador visual profesional en vez del emoji de check
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF00C8FF), CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Text(text = texto, color = Color.White.copy(0.9f), fontSize = 14.sp)
    }
}