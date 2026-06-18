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
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import kotlinx.coroutines.launch
    import pe.tecvote.enrolamiento.data.ClienteRed
    import androidx.compose.ui.draw.*
    import androidx.compose.ui.tooling.preview.Preview
    import pe.tecvote.enrolamiento.ui.Espaciados
    import pe.tecvote.enrolamiento.ui.TamanosAdaptativos
    import pe.tecvote.enrolamiento.ui.EspacioPequeno
    import pe.tecvote.enrolamiento.ui.EspacioMedio
    import pe.tecvote.enrolamiento.ui.EspacioGrande
    import pe.tecvote.enrolamiento.ui.EspacioExtraGrande
    import pe.tecvote.enrolamiento.ui.EspacioEnorme
    import pe.tecvote.enrolamiento.ui.EspacioHorizontalPequeno
    import pe.tecvote.enrolamiento.ui.EspacioHorizontalMedio
    import pe.tecvote.enrolamiento.ui.theme.TECVOTETheme

    @Composable
    fun SlideIngresoDNI(modifier: Modifier = Modifier, onContinuar: (String) -> Unit = {}) {

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
        var dni            by remember { mutableStateOf("") }
        var nombreCompleto by remember { mutableStateOf<String?>(null) }
        var mensajeError   by remember { mutableStateOf<String?>(null) }
        var buscando       by remember { mutableStateOf(false) }
        val scope          = rememberCoroutineScope()

        // ── Animación entrada ─────────────────────────────────────────────
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }
        val alpha by animateFloatAsState(
            targetValue   = if (visible) 1f else 0f,
            animationSpec = tween(1000, easing = LinearOutSlowInEasing),
            label         = "alpha"
        )

        // ── Animaciones infinitas ─────────────────────────────────────────
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

        LaunchedEffect(dni) {
            if (dni.length == 8) {
                buscando       = true
                mensajeError   = null
                nombreCompleto = null
                scope.launch {
                    try {
                        val resp = ClienteRed.api.buscarElector(dni)

                        if (resp.existe) {
                            if (resp.apto) {
                                nombreCompleto = resp.nombre
                            } else {

                                mensajeError = resp.mensaje ?: "El ciudadano no está apto para este proceso."
                            }
                        } else {

                            mensajeError = resp.mensaje ?: "El DNI no está registrado en el sistema."
                        }
                    } catch (e: Exception) {
                        Log.e("TECVOTE_NET", "Error DNI FASE 1: ${e.localizedMessage}", e)
                        mensajeError = "Sin conexión al servidor central TECVOTE."
                    } finally {
                        buscando = false
                    }
                }
            } else {
                nombreCompleto = null
                mensajeError   = null
            }
        }

        // ── UI ────────────────────────────────────────────────────────────
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(degradeFondo)
        ) {
            // Círculos decorativos de fondo
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .offset(x = (-80).dp, y = (-60).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(cyanBrillante.copy(0.06f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 60.dp, y = 60.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(cyanBrillante.copy(0.04f), Color.Transparent)
                        ),
                        CircleShape
                    )
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

                EspacioPequeno()
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
                        Text("TECVOTE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    EspacioMedio()
                    Column {
                        Text("OFICINA NACIONAL DE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Text("PROCESOS ELECTORALES", color = Color.White.copy(0.8f), fontSize = 10.sp, letterSpacing = 0.5.sp)
                    }
                }

                // ── CENTRO ───────────────────────────────────────────────
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {

                    // Logo pequeño con aro cyan
                    val tamanoIcono = TamanosAdaptativos.tamanoIconoCircular()
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier         = Modifier.size(tamanoIcono)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(tamanoIcono * 0.95f)
                                .background(
                                    Brush.radialGradient(
                                        listOf(cyanBrillante.copy(aroAlpha * 0.15f), Color.Transparent)
                                    ),
                                    CircleShape
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .border(1.5.dp, cyanBrillante.copy(aroAlpha * 0.6f), CircleShape)
                        )
                        // Ícono de urna / DNI
                        Text("🪪", fontSize = 36.sp)
                    }

                    EspacioMedio()

                    // Chip TECVOTE
                    Surface(
                        shape  = RoundedCornerShape(6.dp),
                        color  = cyanBrillante.copy(0.15f)
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(5.dp).background(cyanBrillante, CircleShape))
                            EspacioPequeno()
                            Text("TECVOTE", color = cyanBrillante, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                        }
                    }

                    EspacioGrande()

                    // Título dinámico
                    AnimatedContent(
                        targetState   = nombreCompleto != null,
                        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                        label         = "titulo"
                    ) { encontrado ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (!encontrado) {
                                Text(
                                    "INGRESE SU DNI",
                                    color         = Color.White,
                                    fontSize      = 28.sp,
                                    fontWeight    = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    textAlign     = TextAlign.Center
                                )
                                EspacioPequeno()
                                Text(
                                    "Para validar su identidad y acceder a\nla plataforma de votación, por favor\ndigite su número de DNI.",
                                    color     = Color.White.copy(0.65f),
                                    fontSize  = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            } else {
                                Text(
                                    "IDENTIDAD",
                                    color         = Color.White,
                                    fontSize      = 30.sp,
                                    fontWeight    = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    textAlign     = TextAlign.Center
                                )
                                Text(
                                    "ENCONTRADA",
                                    color         = cyanBrillante,
                                    fontSize      = 30.sp,
                                    fontWeight    = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    textAlign     = TextAlign.Center
                                )
                                EspacioPequeno()
                                Text(
                                    "Confirme que sus datos son\ncorrectos para continuar.",
                                    color     = Color.White.copy(0.65f),
                                    fontSize  = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    EspacioGrande()

                    // ── PANEL: Según estado ───────────────────────────────
                    AnimatedContent(
                        targetState   = nombreCompleto != null,
                        transitionSpec = { fadeIn(tween(500)) + slideInVertically { 40 } togetherWith fadeOut(tween(200)) },
                        label         = "panel"
                    ) { encontrado ->
                        if (!encontrado) {

                            // ── FORMULARIO DNI ────────────────────────────
                            Column {
                                // Label
                                Text(
                                    "NÚMERO DE DNI",
                                    color         = Color.White.copy(0.7f),
                                    fontSize      = 11.sp,
                                    fontWeight    = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier      = Modifier.padding(bottom = 8.dp)
                                )

                                // Campo DNI estilo oficial
                                Row(
                                    modifier          = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value         = dni,
                                        onValueChange = { if (it.length <= 8 && it.all(Char::isDigit)) dni = it },
                                        singleLine    = true,
                                        placeholder   = {
                                            Text(
                                                "_ _ _ _ _ _ _ _",
                                                color    = Color.White.copy(0.3f),
                                                fontSize = 18.sp,
                                                letterSpacing = 4.sp
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        isError       = mensajeError != null,
                                        textStyle     = androidx.compose.ui.text.TextStyle(
                                            color         = Color.White,
                                            fontSize      = 22.sp,
                                            fontWeight    = FontWeight.Bold,
                                            letterSpacing = 6.sp
                                        ),
                                        colors        = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor   = cyanBrillante,
                                            unfocusedBorderColor = Color.White.copy(0.25f),
                                            errorBorderColor     = Color(0xFFFF4444),
                                            focusedContainerColor   = Color.White.copy(0.06f),
                                            unfocusedContainerColor = Color.White.copy(0.04f),
                                            cursorColor = cyanBrillante
                                        ),
                                        shape    = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                                        modifier = Modifier.weight(1f).height(60.dp)
                                    )
                                    // Badge "D.V."
                                    Box(
                                        modifier         = Modifier
                                            .height(60.dp)
                                            .width(56.dp)
                                            .background(
                                                Color.White.copy(0.12f),
                                                RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                                            )
                                            .border(
                                                1.dp,
                                                Color.White.copy(0.25f),
                                                RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("D.V.", color = Color.White.copy(0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                EspacioPequeno()
                                Text(
                                    "El dígito de verificación se encuentra al final de su DNI.",
                                    color    = Color.White.copy(0.4f),
                                    fontSize = 11.sp
                                )

                                // Error
                                AnimatedVisibility(visible = mensajeError != null) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                        shape    = RoundedCornerShape(8.dp),
                                        color    = Color(0xFFB71C1C).copy(0.7f)
                                    ) {
                                        Text(
                                            mensajeError ?: "",
                                            color    = Color.White,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }

                                // Loading
                                AnimatedVisibility(visible = buscando) {
                                    Row(
                                        modifier          = Modifier.fillMaxWidth().padding(top = 16.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            color       = cyanBrillante,
                                            modifier    = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                        EspacioMedio()
                                        Text("Verificando...", color = cyanBrillante, fontSize = 13.sp)
                                    }
                                }
                            }

                        } else {

                            // ── PANEL IDENTIDAD ENCONTRADA ────────────────
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(0.07f))
                                    .border(1.dp, cyanBrillante.copy(0.3f), RoundedCornerShape(16.dp))
                                    .padding(20.dp)
                            ) {
                                // Fila DNI
                                Row(
                                    modifier          = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(0.05f))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("DNI", color = Color.White.copy(0.5f), fontSize = 10.sp, letterSpacing = 1.sp)
                                        Text(dni, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp)
                                    }
                                    // Ícono persona
                                    Box(
                                        modifier         = Modifier
                                            .size(40.dp)
                                            .background(cyanBrillante.copy(0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👤", fontSize = 18.sp)
                                    }
                                }

                                EspacioPequeno()

                                // Fila Nombre
                                Row(
                                    modifier          = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(0.05f))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("NOMBRE COMPLETO", color = Color.White.copy(0.5f), fontSize = 10.sp, letterSpacing = 1.sp)
                                        EspacioPequeno()
                                        Text(
                                            nombreCompleto ?: "",
                                            color      = Color.White,
                                            fontSize   = 17.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            lineHeight = 22.sp
                                        )
                                    }
                                    EspacioMedio()
                                    // Check cyan
                                    Box(
                                        modifier         = Modifier
                                            .size(36.dp)
                                            .background(cyanBrillante.copy(0.2f), CircleShape)
                                            .border(1.5.dp, cyanBrillante, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✓", color = cyanBrillante, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // ── BOTTOM ───────────────────────────────────────────────
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.padding(bottom = 0.dp)
                ) {
                    // Botón principal
                    Button(
                        onClick  = {
                            if (nombreCompleto != null) onContinuar(dni)
                        },
                        enabled  = if (nombreCompleto != null) true else (dni.length == 8 && !buscando),
                        shape    = RoundedCornerShape(50.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = Color.Transparent,
                            contentColor           = Color.White,
                            disabledContainerColor = Color.White.copy(0.08f),
                            disabledContentColor   = Color.White.copy(0.3f)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier       = Modifier
                            .fillMaxWidth()
                            .height(TamanosAdaptativos.altoProporcional(0.07))
                            .scale(if (nombreCompleto != null) botonScale else 1f)
                    ) {
                        Box(
                            modifier         = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = if (nombreCompleto != null || (dni.length == 8 && !buscando))
                                        Brush.horizontalGradient(listOf(cyanOscuro, cyanBrillante, cyanOscuro))
                                    else
                                        Brush.horizontalGradient(listOf(Color.White.copy(0.08f), Color.White.copy(0.08f))),
                                    shape = RoundedCornerShape(50.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text          = if (nombreCompleto != null) "CONFIRMAR Y CONTINUAR" else "VERIFICAR DNI",
                                color         = if (nombreCompleto != null || (dni.length == 8 && !buscando)) Color.White else Color.White.copy(0.3f),
                                fontSize      = 15.sp,
                                fontWeight    = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    EspacioMedio()

                    TextButton(onClick = { dni = ""; nombreCompleto = null; mensajeError = null }) {
                        Text(
                            text = if (nombreCompleto != null)
                                "¿Estos no son sus datos? [Cerrar Sesión]"
                            else
                                "¿Problemas con su DNI? Contacte con soporte.",
                            color    = Color.White.copy(0.4f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    EspacioPequeno()

                    // Footer
                    Text(
                        "© 2026 TECVOTE  |  Verificado por Protocolo de Seguridad Avanzado",
                        color     = Color.White.copy(0.25f),
                        fontSize  = 9.sp,
                        textAlign = TextAlign.Center
                    )

                    EspacioPequeno()
                    PuntosIndicadores(total = 6, actual = 1)
                    EspacioPequeno()
                }
            }
        }
    }
