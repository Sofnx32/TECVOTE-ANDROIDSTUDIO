package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.data.ClienteRed
import pe.tecvote.enrolamiento.ui.*
import pe.tecvote.enrolamiento.ui.theme.*
import androidx.compose.foundation.Image

@Composable
fun SlideIngresoDNI(
    modifier: Modifier = Modifier,
    onContinuar: (String) -> Unit = {}
) {
    var dni by remember { mutableStateOf("") }
    var dniBuscado by remember { mutableStateOf<String?>(null) }
    var nombreCompleto by remember { mutableStateOf<String?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var buscando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(dni) {
        if (dni.length == 8 && dni != dniBuscado) {
            buscando = true
            mensajeError = null
            nombreCompleto = null
            dniBuscado = dni
            scope.launch {
                try {
                    val resp = ClienteRed.api.buscarElector(dni)
                    if (resp.existe && resp.apto) {
                        nombreCompleto = resp.nombre
                    } else {
                        mensajeError = resp.mensaje
                    }
                } catch (e: Exception) {
                    mensajeError = context.getString(R.string.sin_conexion_servidor)
                } finally {
                    buscando = false
                }
            }
        } else if (dni.length < 8) {
            dniBuscado = null
            nombreCompleto = null
            mensajeError = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to AzulMedio,
                        0.4f to AzulOscuro,
                        1.0f to AzulProfundo
                    )
                )
            )
    ) {
        // Orbes de luz
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(listOf(CyanBrillante.copy(0.06f), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(listOf(CyanBrillante.copy(0.04f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.06f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween  // ← Esto separa logo y texto
            ) {
                // ← IZQUIERDA: IMAGEN DEL LOGO
                Image(
                    painter = painterResource(id = R.drawable.logotecvote),  // ← CAMBIA por tu imagen
                    contentDescription = "Logo institucional",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // → DERECHA/CENTRO: TEXTO
                Column(
                    modifier = Modifier.weight(1f),  // ← Esto centra el texto
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.oficina_nacional),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.procesos_electorales),
                        color = Color.White.copy(0.8f),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            EspacioGrande()

            // CENTRO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Icono animado
                val inf = rememberInfiniteTransition(label = "inf")
                val aroAlpha by inf.animateFloat(
                    initialValue = 0.4f, targetValue = 0.9f,
                    animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
                    label = "aro"
                )

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(
                                Brush.radialGradient(listOf(CyanBrillante.copy(aroAlpha * 0.15f), Color.Transparent)),
                                CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .border(1.5.dp, CyanBrillante.copy(aroAlpha * 0.6f), CircleShape)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_dni_tarjeta),
                        contentDescription = "DNI de ejemplo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),  // Tamaño más grande
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Badge
                Surface(shape = RoundedCornerShape(6.dp), color = CyanBrillante.copy(0.15f)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(5.dp).background(CyanBrillante, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.tecvote),
                            color = CyanBrillante,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Título animado
                AnimatedContent(
                    targetState = nombreCompleto != null,
                    transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                    label = "titulo"
                ) { encontrado ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (!encontrado) {
                            Text(
                                text = stringResource(R.string.ingrese_dni),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.para_validar),
                                color = Color.White.copy(0.65f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.identidad),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = stringResource(R.string.encontrada),
                                color = CyanBrillante,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.confirme_datos),
                                color = Color.White.copy(0.65f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Panel animado
                AnimatedContent(
                    targetState = nombreCompleto != null,
                    transitionSpec = {
                        fadeIn(tween(500)) + slideInVertically { 40 } togetherWith fadeOut(tween(200))
                    },
                    label = "panel"
                ) { encontrado ->
                    if (!encontrado) {
                        // INPUT DNI
                        Column {
                            Text(
                                text = stringResource(R.string.numero_dni),
                                color = Color.White.copy(0.7f),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val borderColor = if (mensajeError != null) Color(0xFFFF4444) else Color.White.copy(0.25f)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(0.04f), RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                OutlinedTextField(
                                    value = dni,
                                    onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) dni = it },
                                    singleLine = true,
                                    placeholder = {
                                        Text(
                                            text = stringResource(R.string.dni),
                                            color = Color.White.copy(0.3f),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    isError = mensajeError != null,
                                    textStyle = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 6.sp
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        errorBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = CyanBrillante
                                    ),
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(Color.White.copy(0.25f))
                                )
                                Box(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .fillMaxHeight()
                                        .background(Color.White.copy(0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.dv),
                                        color = Color.White.copy(0.6f),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.problemas_dni),
                                color = Color.White.copy(0.5f),
                                style = MaterialTheme.typography.labelSmall
                            )

                            AnimatedVisibility(visible = mensajeError != null) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFB71C1C).copy(0.8f)
                                ) {
                                    Text(
                                        text = mensajeError ?: "",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = buscando) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        color = CyanBrillante,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.buscando_rostro),
                                        color = CyanBrillante,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    } else {
                        // TARJETA USUARIO
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(0.07f))
                                .border(1.dp, CyanBrillante.copy(0.3f), RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(0.05f))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = stringResource(R.string.dni), color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                                    Text(text = dni, color = Color.White, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(CyanBrillante.copy(0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_usuario_avatar),
                                        contentDescription = "Avatar de usuario",
                                        modifier = Modifier
                                            .size(20.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(0.05f))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = stringResource(R.string.nombre_completo), color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = nombreCompleto ?: "",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(CyanBrillante.copy(0.2f), CircleShape)
                                        .border(1.5.dp, CyanBrillante, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_check_simple),
                                        contentDescription = "Datos verificados",
                                        modifier = Modifier.size(16.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // FOOTER
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val isEnabled = nombreCompleto != null || (dni.length == 8 && !buscando)
                val buttonBrush = if (isEnabled) {
                    Brush.horizontalGradient(listOf(CyanOscuro, CyanBrillante, CyanOscuro))
                } else {
                    Brush.horizontalGradient(listOf(Color.White.copy(0.08f), Color.White.copy(0.08f)))
                }

                Button(
                    onClick = { if (nombreCompleto != null) onContinuar(dni) },
                    enabled = isEnabled,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = buttonBrush, shape = RoundedCornerShape(50.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (nombreCompleto != null) stringResource(R.string.confirmar_continuar) else stringResource(R.string.registrar_rostro),
                            color = if (isEnabled) Color.White else Color.White.copy(0.3f),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        dni = ""
                        nombreCompleto = null
                        mensajeError = null
                        dniBuscado = null
                    }
                ) {
                    Text(
                        text = if (nombreCompleto != null) stringResource(R.string.no_son_datos) else stringResource(R.string.problemas_dni),
                        color = Color.White.copy(0.5f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.seguridad_protocolo),
                    color = Color.White.copy(0.3f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}