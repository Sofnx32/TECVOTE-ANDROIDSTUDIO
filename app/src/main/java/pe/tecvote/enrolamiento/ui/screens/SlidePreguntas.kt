package pe.tecvote.enrolamiento.ui.screens

import android.util.Log
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.BodyPreguntas
import pe.tecvote.enrolamiento.data.ClienteRed
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos
import java.text.Normalizer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue


fun normalizarTexto(texto: String): String =
    Normalizer.normalize(texto.trim().uppercase(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")


/**
 * Formatea la fecha DD/MM/AAAA manteniendo la posición correcta del cursor.
 *
 * PROBLEMA ORIGINAL:
 * - Al formatear el texto, el cursor saltaba porque se reconstruía el string completo
 * - Ejemplo: Escribir "27091974" → Cursor saltaba antes del 0 → Resultado "27/90/1974"
 *
 * SOLUCIÓN:
 * - Usamos TextFieldValue que incluye texto + posición del cursor
 * - Calculamos dónde debe estar el cursor después de agregar los slashes
 */
fun formatearFechaConCursor(valorActual: TextFieldValue): TextFieldValue {
    // Extraer solo dígitos del texto actual (sin slashes)
    val soloDigitos = valorActual.text.filter { it.isDigit() }

    // Limitar a 8 dígitos máximo
    val digitosLimitados = soloDigitos.take(8)

    // Construir texto formateado con slashes
    val textoFormateado = buildString {
        digitosLimitados.forEachIndexed { index, digito ->
            if (index == 2 || index == 4) append('/')
            append(digito)
        }
    }

    // Calcular nueva posición del cursor
    // El cursor debe avanzar 1 posición extra por cada slash que se agregó
    val posicionOriginal = valorActual.selection.start
    val cantidadSlashesAgregados = textoFormateado.count { it == '/' }

    // Nueva posición: posición original + cantidad de slashes antes de esa posición
    var nuevaPosicion = posicionOriginal
    var slashesContados = 0
    for (i in 0 until nuevaPosicion.coerceAtMost(textoFormateado.length)) {
        if (textoFormateado[i] == '/') {
            slashesContados++
            // Si el cursor estaba justo después de un slash, no lo movemos
            if (i >= posicionOriginal) break
        }
    }

    // Ajustar posición: por cada 2 dígitos escritos, se agrega 1 slash
    val digitosEscritos = digitosLimitados.take(posicionOriginal).length
    val ajusteCursor = when {
        digitosEscritos > 4 -> 2  // Después de AAAA ya hay 2 slashes
        digitosEscritos > 2 -> 1  // Después de MM ya hay 1 slash
        else -> 0
    }

    nuevaPosicion = (posicionOriginal + ajusteCursor).coerceAtMost(textoFormateado.length)

    return TextFieldValue(
        text = textoFormateado,
        selection = TextRange(nuevaPosicion)
    )
}

@Composable
fun SlidePreguntas(
    modifier: Modifier = Modifier,
    dni: String = "",
    onContinuar: () -> Unit = {},
) {
    // ── Colores ONPE ─────────────────────────────────────────────────
    val azulProfundo  = Color(0xFF020B18)
    val azulOscuro    = Color(0xFF041529)
    val azulMedio     = Color(0xFF0A2547)
    val cyanBrillante = Color(0xFF00C8FF)
    val cyanOscuro    = Color(0xFF0090CC)

    val degradeFondo = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to azulMedio,
            0.4f to azulOscuro,
            1.0f to azulProfundo
        )
    )

    // ── Estados ───────────────────────────────────────────────────────
    var nombrePadre     by remember { mutableStateOf("") }
    var nombreMadre     by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf(TextFieldValue("")) }
    var validando       by remember { mutableStateOf(false) }
    var error           by remember { mutableStateOf<String?>(null) }
    var intentos        by remember { mutableStateOf(0) }
    var bloqueado       by remember { mutableStateOf(false) }
    var confirmado      by remember { mutableStateOf(false) }
    val scope           = rememberCoroutineScope()


    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing),
        label         = "alpha"
    )

    val inf = rememberInfiniteTransition(label = "inf")
    val botonScale by inf.animateFloat(
        initialValue  = 1f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label         = "boton"
    )
    val aroAlpha by inf.animateFloat(
        initialValue  = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label         = "aro"
    )

    val formularioValido = nombrePadre.isNotBlank() && nombreMadre.isNotBlank() &&
            Regex("""\d{2}/\d{2}/\d{4}""").matches(fechaNacimiento.text)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
    ) {
        // Círculos decorativos
        Box(
            modifier = Modifier
                .size(350.dp).offset(x = (-80).dp, y = (-60).dp)
                .background(Brush.radialGradient(listOf(cyanBrillante.copy(0.06f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp).align(Alignment.BottomEnd).offset(x = 60.dp, y = 60.dp)
                .background(Brush.radialGradient(listOf(cyanBrillante.copy(0.04f), Color.Transparent)), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = TamanosAdaptativos.paddingHorizontalPantalla())
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(Modifier.height(8.dp))
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.06f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tecvote", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("PROCESOS ELECTORALES", color = Color.White.copy(0.8f), fontSize = 10.sp, letterSpacing = 0.5.sp)
                }
            }

            // ── CENTRO ───────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(TamanosAdaptativos.tamanoImagenSecundaria())) {
                    Box(
                        modifier = Modifier.size(TamanosAdaptativos.tamanoImagenSecundaria() * 0.94f)
                            .background(Brush.radialGradient(listOf(cyanBrillante.copy(aroAlpha * 0.15f), Color.Transparent)), CircleShape)
                    )
                    Box(modifier = Modifier.size(TamanosAdaptativos.tamanoImagenSecundaria() * 0.75f).border(1.5.dp, cyanBrillante.copy(aroAlpha * 0.6f), CircleShape))
                    Text("🔐", fontSize = 24.sp)
                }

                Spacer(Modifier.height(10.dp))

                // Chip
                Surface(shape = RoundedCornerShape(6.dp), color = cyanBrillante.copy(0.15f)) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(5.dp).background(cyanBrillante, CircleShape))
                        Spacer(Modifier.width(6.dp))
                        Text("TECVOTE", color = cyanBrillante, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Título
                Text("CONFIRMACIÓN DE", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                Text("DATOS PERSONALES", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Para su seguridad, verifique los\nsiguientes datos adicionales.",
                    color = Color.White.copy(0.6f), fontSize = 12.sp,
                    textAlign = TextAlign.Center, lineHeight = 16.sp
                )

                Spacer(Modifier.height(16.dp))

                if (bloqueado) {
                    Surface(
                        shape  = RoundedCornerShape(14.dp),
                        color  = Color(0xFFB71C1C).copy(0.85f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier            = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚠️", fontSize = 32.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "SESIÓN BLOQUEADA",
                                color = Color.White, fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp, letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Múltiples intentos incorrectos detectados. Contacte con soporte electoral.",
                                color = Color.White.copy(0.8f), fontSize = 12.sp,
                                textAlign = TextAlign.Center, lineHeight = 18.sp
                            )
                        }
                    }
                } else {

                    AnimatedContent(
                        targetState   = confirmado,
                        transitionSpec = { fadeIn(tween(500)) + slideInVertically { 30 } togetherWith fadeOut(tween(200)) },
                        label         = "panel"
                    ) { estaConfirmado ->

                        if (!estaConfirmado) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(0.05f))
                                    .border(1.dp, cyanBrillante.copy(0.2f), RoundedCornerShape(16.dp))
                                    .padding(16.dp) // Padding un poco reducido para que entre bien
                            ) {

                                Text(
                                    "NOMBRE DEL PADRE",
                                    color = Color.White.copy(0.5f), fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value         = nombrePadre,
                                    onValueChange = { nombrePadre = it },
                                    placeholder   = { Text("Ingresa el primer nombre", color = Color.White.copy(0.3f), fontSize = 13.sp) },
                                    singleLine    = true,
                                    colors        = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor      = cyanBrillante,
                                        unfocusedBorderColor    = Color.White.copy(0.2f),
                                        focusedTextColor        = Color.White,
                                        unfocusedTextColor      = Color.White,
                                        focusedContainerColor   = Color.White.copy(0.06f),
                                        unfocusedContainerColor = Color.White.copy(0.03f),
                                        cursorColor             = cyanBrillante
                                    ),
                                    shape    = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(TamanosAdaptativos.altoProporcional(0.065))
                                )

                                Spacer(Modifier.height(10.dp))

                                // Campo: Nombre de la madre
                                Text(
                                    "NOMBRE DE LA MADRE",
                                    color = Color.White.copy(0.5f), fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value         = nombreMadre,
                                    onValueChange = { nombreMadre = it },
                                    placeholder   = { Text("Ingresa el primer nombre", color = Color.White.copy(0.3f), fontSize = 13.sp) },
                                    singleLine    = true,
                                    colors        = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor      = cyanBrillante,
                                        unfocusedBorderColor    = Color.White.copy(0.2f),
                                        focusedTextColor        = Color.White,
                                        unfocusedTextColor      = Color.White,
                                        focusedContainerColor   = Color.White.copy(0.06f),
                                        unfocusedContainerColor = Color.White.copy(0.03f),
                                        cursorColor             = cyanBrillante
                                    ),
                                    shape    = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(TamanosAdaptativos.altoProporcional(0.065))
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    "FECHA DE NACIMIENTO",
                                    color = Color.White.copy(0.5f), fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value         = fechaNacimiento,
                                    onValueChange = { fechaNacimiento = formatearFechaConCursor(it) },
                                    placeholder   = { Text("DD / MM / AAAA", color = Color.White.copy(0.3f), fontSize = 13.sp) },
                                    singleLine    = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors        = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor      = cyanBrillante,
                                        unfocusedBorderColor    = Color.White.copy(0.2f),
                                        focusedTextColor        = Color.White,
                                        unfocusedTextColor      = Color.White,
                                        focusedContainerColor   = Color.White.copy(0.06f),
                                        unfocusedContainerColor = Color.White.copy(0.03f),
                                        cursorColor             = cyanBrillante
                                    ),
                                    shape    = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(TamanosAdaptativos.altoProporcional(0.065))
                                )

                                // Error
                                AnimatedVisibility(visible = error != null) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                        shape    = RoundedCornerShape(8.dp),
                                        color    = Color(0xFFB71C1C).copy(0.7f)
                                    ) {
                                        Text(
                                            "$error  (Intento $intentos / 3)",
                                            color    = Color.White,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                        } else {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(0.05f))
                                    .border(1.dp, cyanBrillante.copy(0.3f), RoundedCornerShape(16.dp))
                                    .padding(4.dp)
                            ) {
                                // Fila: Nombre del padre
                                FilaConfirmada(
                                    etiqueta = "NOMBRE DEL PADRE",
                                    valor    = nombrePadre.trim().uppercase(),
                                    cyan     = cyanBrillante
                                )
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.08f)))

                                // Fila: Nombre de la madre
                                FilaConfirmada(
                                    etiqueta = "NOMBRE DE LA MADRE",
                                    valor    = nombreMadre.trim().uppercase(),
                                    cyan     = cyanBrillante
                                )
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(0.08f)))

                                // Fila: Fecha
                                FilaConfirmada(
                                    etiqueta = "FECHA DE NACIMIENTO",
                                    valor    = fechaNacimiento.text.replace("/", " / "),
                                    cyan     = cyanBrillante
                                )
                            }
                        }
                    }
                }
            }

            // ── BOTTOM ───────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                if (!bloqueado) {
                    Button(
                        onClick = {
                            if (confirmado) {

                                onContinuar()
                            } else {
                                scope.launch {
                                    validando = true
                                    error     = null
                                    try {
                                        // ── TRADUCCIÓN DE FECHA CRÍTICA ──
                                        // Convierte "DD/MM/AAAA" escrito en la tablet a "AAAA-MM-DD" para Django
                                        // fechaNacimiento ahora es TextFieldValue, usamos .text
                                        val partesFecha = fechaNacimiento.text.split("/")
                                        val fechaFormatoDjango = if (partesFecha.size == 3) {
                                            "${partesFecha[2]}-${partesFecha[1]}-${partesFecha[0]}"
                                        } else {
                                            fechaNacimiento.text.trim()
                                        }

                                        val resp = ClienteRed.api.validarPreguntas(
                                            BodyPreguntas(
                                                dni              = dni,
                                                nombre_padre     = normalizarTexto(nombrePadre),
                                                nombre_madre     = normalizarTexto(nombreMadre),
                                                fecha_nacimiento = fechaFormatoDjango // ¡Sincronizado!
                                            )
                                        )

                                        if (resp.valido) {
                                            confirmado = true   // Muestra el panel de datos confirmados en verde
                                        } else {
                                            intentos++
                                            if (intentos >= 3) {
                                                bloqueado = true
                                            } else {
                                                error = "Datos incorrectos. Verifica tu información e intenta de nuevo."
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("TECVOTE_NET", "Error en Fase 1.5 Cuestionario: ${e.message}", e)
                                        error = "Sin conexión con el servidor central ONPE."
                                    } finally {
                                        validando = false
                                    }
                                }
                            }
                        },
                        enabled  = if (confirmado) true else (formularioValido && !validando),
                        shape    = RoundedCornerShape(50.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = Color.Transparent,
                            contentColor           = Color.White,
                            disabledContainerColor = Color.White.copy(0.06f),
                            disabledContentColor   = Color.White.copy(0.25f)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier       = Modifier
                            .fillMaxWidth()
                            .height(TamanosAdaptativos.altoProporcional(0.07))
                            .scale(if (confirmado || formularioValido) botonScale else 1f)
                    ) {
                        Box(
                            modifier         = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = if (confirmado || (formularioValido && !validando))
                                        Brush.horizontalGradient(listOf(cyanOscuro, cyanBrillante, cyanOscuro))
                                    else
                                        Brush.horizontalGradient(listOf(Color.White.copy(0.06f), Color.White.copy(0.06f))),
                                    shape = RoundedCornerShape(50.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (validando) {
                                CircularProgressIndicator(
                                    color       = Color.White,
                                    modifier    = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text          = "CONFIRMAR Y CONTINUAR",
                                    color         = if (confirmado || (formularioValido && !validando)) Color.White else Color.White.copy(0.25f),
                                    fontSize      = 15.sp,
                                    fontWeight    = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    TextButton(onClick = {
                        nombrePadre     = ""
                        nombreMadre     = ""
                        fechaNacimiento = TextFieldValue("")  // ← Ahora es TextFieldValue
                        confirmado      = false
                        error           = null
                        intentos        = 0
                    })  {
                        Text(
                            "¿Estos no son sus datos? [Cerrar Sesión]",
                            color = Color.White.copy(0.4f), fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Fila de dato confirmado con check cyan ────────────────────────────
@Composable
private fun FilaConfirmada(etiqueta: String, valor: String, cyan: Color) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.04f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(etiqueta, color = Color.White.copy(0.45f), fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(valor, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 20.sp)
        }
        Spacer(Modifier.width(12.dp))
        Box(
            modifier         = Modifier
                .size(30.dp)
                .background(cyan.copy(0.18f), CircleShape)
                .border(1.5.dp, cyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", color = cyan, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
