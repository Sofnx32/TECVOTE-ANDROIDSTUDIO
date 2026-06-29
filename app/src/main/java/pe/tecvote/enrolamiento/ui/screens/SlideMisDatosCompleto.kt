package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.data.RespuestaMisDatos
import pe.tecvote.enrolamiento.data.ElectorData
import pe.tecvote.enrolamiento.data.MesaData
import pe.tecvote.enrolamiento.data.LocalVotacionData
import pe.tecvote.enrolamiento.data.MiembroMesaData
import pe.tecvote.enrolamiento.ui.*

@Composable
fun SlideMisDatosCompleto(
    modifier: Modifier = Modifier,
    datos: RespuestaMisDatos,
    onDescargarConstancia: (String?) -> Unit = {}
) {
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

    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = LinearOutSlowInEasing),
        label = "fade_in"
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
                .alpha(alpha)
        ) {
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
                    // CORREGIDO: "ONPE" ahora se maneja de forma segura o explícita como texto de marca
                    Text(text = "ONPE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    // CORREGIDO: Eliminación de concatenación literal de strings en favor de recursos compuestos o limpios
                    Text(
                        text = stringResource(R.string.oficina_nacional) + " " + stringResource(R.string.procesos_electorales),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = stringResource(R.string.sistema_nacional_enrolamiento), color = Color.White.copy(0.6f), fontSize = 9.sp)
                }
            }

            EspacioGrande()

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
                            text = stringResource(R.string.documento_identidad, elector.dni),
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
                                Text(text = stringResource(R.string.verificado), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.biometria_ok), color = Color.White.copy(0.7f), fontSize = 8.sp)
                            }
                        }
                    }
                }
            }

            EspacioGrande()

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

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onDescargarConstancia(datos.codigoConstancia) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, cyanBrillante.copy(0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = cyanBrillante)
                ) {
                    Text(text = stringResource(R.string.descargar_constancia), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            EspacioMedio()
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
                text = stringResource(R.string.datos_filiatorios),
                color = cyanBrillante,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            EspacioMedio()
            FilaDatoPersonal(stringResource(R.string.apellidos_nombres), elector.nombreCompleto)
            FilaDatoPersonal(stringResource(R.string.dni), elector.dni)
            FilaDatoPersonal(stringResource(R.string.condicion_enrolamiento), elector.estadoEnrolamiento)
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
            Text(text = stringResource(R.string.asignacion_geografica), color = cyanBrillante, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            EspacioMedio()
            FilaDatoPersonal(stringResource(R.string.numero_mesa), mesa?.codigo ?: stringResource(R.string.no_registrado))
            FilaDatoPersonal(
                stringResource(R.string.ubicacion_centro_mesa),
                stringResource(R.string.aula_piso, mesa?.aula ?: stringResource(R.string.no_asignado), mesa?.piso ?: stringResource(R.string.no_asignado))
            )
            FilaDatoPersonal(stringResource(R.string.centro_votacion), local?.nombre ?: stringResource(R.string.no_asignado))
            FilaDatoPersonal(stringResource(R.string.direccion_local), local?.direccion ?: stringResource(R.string.no_asignada))
            FilaDatoPersonal(stringResource(R.string.ubigeo_geografico), local?.ubigeo ?: stringResource(R.string.na))
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
                Text(text = stringResource(R.string.condicion_miembro), color = cyanBrillante, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    text = if (miembro.esMiembro) stringResource(R.string.designado) else stringResource(R.string.no_seleccionado),
                    color = if (miembro.esMiembro) cyanBrillante else Color.White.copy(0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            if (miembro.esMiembro) {
                EspacioMedio()
                FilaDatoPersonal(stringResource(R.string.cargo_asignado), miembro.cargo)
                FilaDatoPersonal(stringResource(R.string.horario_obligacion), miembro.horario ?: stringResource(R.string.no_asignado))
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
        Text(text = label, color = Color.White.copy(0.5f), fontSize = 11.sp)
        Text(text = valor, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, textAlign = TextAlign.End)
    }
}