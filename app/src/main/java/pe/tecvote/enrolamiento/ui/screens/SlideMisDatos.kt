package pe.tecvote.enrolamiento.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.BodyCambioSede
import pe.tecvote.enrolamiento.data.ClienteRed
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.OutlinedButton
import kotlinx.coroutines.delay
import pe.tecvote.enrolamiento.ui.EspacioExtraGrande
import pe.tecvote.enrolamiento.ui.EspacioGrande
import pe.tecvote.enrolamiento.ui.EspacioMedio
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos


data class LocalVotacionItem(
    val id: String,
    val nombre: String,
    val direccion: String
)

private sealed class EstadoSeleccion {
    object Cargando : EstadoSeleccion()
    object YaAsignado : EstadoSeleccion()
    data class ListaDisponible(val locales: List<LocalVotacionItem>) : EstadoSeleccion()
    data class Error(val mensaje: String) : EstadoSeleccion()
}

@Composable
fun SlideMisDatos(
    dni: String,
    token: String?,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ── Paleta idéntica al SlideBiometrica ──────────────────────────────────
    val verdeProfundo   = Color(0xFF00251A)
    val verdeOscuro     = Color(0xFF00332A)
    val verdeMedio      = Color(0xFF004D40)
    val cyanBrillante   = Color(0xFF00E5FF)
    val accentVerde     = Color(0xFF1DE9B6)

    val degradeFondo = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to verdeMedio,
            0.4f to verdeOscuro,
            1.0f to verdeProfundo
        )
    )

    // ── Estado de la pantalla ────────────────────────────────────────────────
    var estadoPantalla      by remember { mutableStateOf<EstadoSeleccion>(EstadoSeleccion.Cargando) }
    var localSeleccionado   by remember { mutableStateOf<LocalVotacionItem?>(null) }
    var enviando            by remember { mutableStateOf(false) }
    var resultadoMensaje    by remember { mutableStateOf<String?>(null) }
    var esExito             by remember { mutableStateOf(false) }
    var confirmacionOk      by remember { mutableStateOf(false) }

    // 🔹 NUEVOS: Para QR y mapa
    var qrBase64 by remember { mutableStateOf<String?>(null) }
    var qrTexto by remember { mutableStateOf<String?>(null) }
    var coordenadasLocal by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var nombreLocal by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoMapa by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ── Animación de entrada ─────────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue    = if (visible) 1f else 0f,
        animationSpec  = tween(900, easing = LinearOutSlowInEasing),
        label          = "alpha_entrada"
    )

    // ── Animaciones infinitas ─────────────────────────────────────────────────
    val inf = rememberInfiniteTransition(label = "inf_local")

    val alfaPulso by inf.animateFloat(
        initialValue  = 0.35f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label         = "alfa_pulso"
    )
    val botonScale by inf.animateFloat(
        initialValue  = 1f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label         = "boton_scale"
    )
    val aroAlpha by inf.animateFloat(
        initialValue  = 0.3f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label         = "aro_alpha"
    )

    // ── Cargar datos del elector (API mejorada) ──────────────────────────────
    LaunchedEffect(dni, token) {
        if (token == null) {
            estadoPantalla = EstadoSeleccion.Error("Autenticación requerida")
            return@LaunchedEffect
        }

        Log.d("TECVOTE", "🔍 Cargando datos para DNI: $dni")

        // 🔹 ESPERAR a que haya token válido (máximo 5 segundos)
        var token = ClienteRed.tokenSesionBearer
        var intentos = 0

        while (token == null && intentos < 10) {
            Log.d("TECVOTE", "⏳ Esperando token... intento ${intentos + 1}/10")
            delay(500)  // Esperar 500ms entre intentos
            token = ClienteRed.tokenSesionBearer
            intentos++
        }

        if (token == null) {
            Log.e("TECVOTE", "❌ No se obtuvo token después de esperar")
            estadoPantalla = EstadoSeleccion.Error("Esperando autenticación...")
            return@LaunchedEffect
        }

        Log.d("TECVOTE", "🔑 Token obtenido: ${token.take(20)}...")

        // ✅ AHORA SÍ: Cargar datos con token válido
        estadoPantalla = EstadoSeleccion.Cargando
        try {
            val response = ClienteRed.api.getMisDatosElector(
                dni = dni,
                token = "Bearer $token"
            )

            Log.d("TECVOTE", "📡 Respuesta getMisDatosElector: status=${response.status}")

            if (response.status == "ok") {
                // ✅ NUEVA API: Procesar respuesta completa con QR
                val elector = response.elector
                val localVotacion = response.local_votacion
                val localPreferido = response.local_preferido

                // Guardar datos para UI
                qrBase64 = response.qr_base64
                qrTexto = response.qr_texto

                // Determinar qué local mostrar
                val local = localVotacion ?: localPreferido
                if (local != null) {
                    val lat = local["latitud"] as? Double
                    val lng = local["longitud"] as? Double
                    coordenadasLocal = if (lat != null && lng != null) Pair(lat, lng) else null
                    nombreLocal = local["nombre"] as? String

                    // Si tiene mesa asignada → YaAsignado, si solo preferencia → ListaDisponible
                    estadoPantalla = if (localVotacion != null) {
                        EstadoSeleccion.YaAsignado
                    } else {
                        // Crear lista con el local preferido para compatibilidad
                        val item = LocalVotacionItem(
                            id = local["id"] as? String ?: "",
                            nombre = local["nombre"] as? String ?: "",
                            direccion = local["direccion"] as? String ?: ""
                        )
                        EstadoSeleccion.ListaDisponible(listOf(item))
                    }
                } else {
                    // No tiene local → mostrar opción para seleccionar
                    estadoPantalla = EstadoSeleccion.ListaDisponible(emptyList())
                }
            } else {
                // 🔹 FALLBACK: Usar API antigua getLugarVotacion
                Log.d("TECVOTE", "⚠️ Fallback a getLugarVotacion...")
                val resp = ClienteRed.api.getLugarVotacion(dni)
                Log.d("TECVOTE", "📡 getLugarVotacion response: ${resp.estado_logistica}")

                when (resp.estado_logistica) {
                    "ASIGNADO" -> {
                        Log.d("TECVOTE", "✅ Local YA ASIGNADO")
                        estadoPantalla = EstadoSeleccion.YaAsignado
                    }
                    "BUSQUEDA_ACTIVA" -> {
                        Log.d("TECVOTE", "🔍 BUSQUEDA_ACTIVA - locales: ${resp.locales_para_elegir?.size ?: 0}")
                        val lista = resp.locales_para_elegir
                            ?.map { item ->
                                Log.d("TECVOTE", "✅ Local cargado: ${item.nombre}")
                                LocalVotacionItem(
                                    id = item.id,
                                    nombre = item.nombre ?: "Sin nombre",  // ← Agregar valor por defecto
                                    direccion = item.direccion ?: "Dirección no disponible"  // ← Agregar valor por defecto
                                )
                            } ?: emptyList()

                        estadoPantalla = if (lista.isEmpty()) {
                            Log.e("TECVOTE", "❌ No se encontraron locales")
                            EstadoSeleccion.Error("No se encontraron locales activos para tu Ubigeo legal.")
                        } else {
                            Log.d("TECVOTE", "✅ ${lista.size} locales encontrados")
                            EstadoSeleccion.ListaDisponible(lista)
                        }
                    }
                    else -> {
                        Log.e("TECVOTE", "❌ Estado desconocido: ${resp.estado_logistica}")
                        estadoPantalla = EstadoSeleccion.Error(
                            resp.mensaje ?: "Estado de logística desconocido."
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TECVOTE", "❌ Error: ${e.message}")
            e.printStackTrace()
            estadoPantalla = EstadoSeleccion.Error("Error de conexión.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COMPOSICIÓN PRINCIPAL
    // ─────────────────────────────────────────────────────────────────────────
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
    ) {
        // Orbes decorativos de fondo
        Box(
            modifier = Modifier
                .size(360.dp)
                .offset(x = (-90).dp, y = (-70).dp)
                .background(
                    Brush.radialGradient(listOf(cyanBrillante.copy(0.06f), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 70.dp, y = 70.dp)
                .background(
                    Brush.radialGradient(listOf(accentVerde.copy(0.05f), Color.Transparent)),
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
            Spacer(Modifier.height(8.dp))

            // ── Header TECVOTE ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.06f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("TEC", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "SISTEMA DE ENROLAMIENTO",
                        color       = Color.White,
                        fontSize    = 11.sp,
                        fontWeight  = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "MIS DATOS",
                        color       = cyanBrillante,
                        fontSize    = 10.sp,
                        fontWeight  = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // ── Cuerpo central animado según el Estado de Red ────────────────
            Column(
                modifier              = Modifier.weight(1f),
                horizontalAlignment   = Alignment.CenterHorizontally,
                verticalArrangement   = Arrangement.Center
            ) {
                when {
                    confirmacionOk -> {
                        ConfirmacionExitosa(cyanBrillante)
                    }
                    estadoPantalla is EstadoSeleccion.YaAsignado -> {
                        // 🔹 MOSTRAR QR si existe
                        if (!qrBase64.isNullOrEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                LocalYaAsignado(
                                    cyanBrillante = cyanBrillante,
                                    aroAlpha      = aroAlpha
                                )
                                Spacer(Modifier.height(16.dp))

                                // Card con QR
                                Card(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🎫 Constancia de Enrolamiento",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(8.dp))

                                        // Decodificar QR de forma segura
                                        val qrBitmap = remember(qrBase64) {
                                            try {
                                                qrBase64?.let { base64 ->
                                                    val base64Data = base64.replace("data:image/png;base64,", "")
                                                    val qrBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                                                    android.graphics.BitmapFactory.decodeByteArray(qrBytes, 0, qrBytes.size)
                                                }
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }

                                        // Mostrar QR o mensaje
                                        if (qrBitmap != null) {
                                            Image(
                                                bitmap = qrBitmap.asImageBitmap(),
                                                contentDescription = "Código QR",
                                                modifier = Modifier
                                                    .height(TamanosAdaptativos.altoProporcional(0.07))
                                                    .background(Color.White, RoundedCornerShape(8.dp))
                                                    .padding(4.dp)
                                            )
                                            qrTexto?.let { texto ->
                                                Text(texto, fontSize = 9.sp, color = Color.White.copy(0.7f), textAlign = TextAlign.Center)
                                            }
                                        } else {
                                            Text("QR no disponible", fontSize = 12.sp, color = Color.White.copy(0.5f))
                                        }
                                    }
                                }

                                // Botón para abrir mapa si hay coordenadas
                                coordenadasLocal?.let { (lat, lng) ->
                                    Spacer(Modifier.height(12.dp))
                                    OutlinedButton(
                                        onClick = {
                                            // Abrir Google Maps externo con Intent (sin SDK pesado)
                                            val uri = android.net.Uri.parse("geo:$lat,$lng?q=${nombreLocal ?: "mi local"}")
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                            intent.setPackage("com.google.android.apps.maps")
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // Fallback: abrir en navegador
                                                context.startActivity(
                                                    android.content.Intent(
                                                        android.content.Intent.ACTION_VIEW,
                                                        android.net.Uri.parse("https://maps.google.com/?q=$lat,$lng")
                                                    )
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .height(48.dp),
                                        border = BorderStroke(1.dp, cyanBrillante)
                                    ) {
                                        Text("🗺️ Ver ubicación en Google Maps")
                                    }
                                }
                            }
                        } else {
                            // Sin QR → mostrar versión original
                            LocalYaAsignado(
                                cyanBrillante = cyanBrillante,
                                aroAlpha      = aroAlpha
                            )
                        }
                    }
                    estadoPantalla is EstadoSeleccion.Cargando -> {
                        CargandoLocales(cyanBrillante = cyanBrillante, alfaPulso = alfaPulso)
                    }
                    estadoPantalla is EstadoSeleccion.Error -> {
                        MensajeError(
                            mensaje       = (estadoPantalla as EstadoSeleccion.Error).mensaje,
                            cyanBrillante = cyanBrillante
                        )
                    }
                    estadoPantalla is EstadoSeleccion.ListaDisponible -> {
                        val locales = (estadoPantalla as EstadoSeleccion.ListaDisponible).locales

                        // 🔹 Si no hay locales y no tiene QR → mostrar opción para seleccionar local
                        if (locales.isEmpty() && qrBase64.isNullOrEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "⚠️ Aún no has seleccionado un local de votación",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = { mostrarDialogoMapa = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = cyanBrillante)
                                ) {
                                    Text("🗺️ Seleccionar mi local", color = verdeProfundo, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Lista normal de locales
                            ListaLocales(
                                locales              = locales,
                                localSeleccionado    = localSeleccionado,
                                cyanBrillante        = cyanBrillante,
                                accentVerde          = accentVerde,
                                resultadoMensaje     = resultadoMensaje,
                                esExito              = esExito,
                                onSeleccionar        = { localSeleccionado = it }
                            )
                        }
                    }
                }
            }

            // ── Botón de acción principal Inteligente ─────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                val textoBoton = when {
                    confirmacionOk                               -> "CONTINUAR PROCESO →"
                    estadoPantalla is EstadoSeleccion.YaAsignado -> "CONTINUAR →"
                    enviando                                     -> "ASIGNANDO MESA EN VIVO..."
                    localSeleccionado != null                    -> "CONFIRMAR MI LOCAL Y MESA →"
                    else                                         -> "SELECCIONA UN LOCAL"
                }

                val botonHabilitado = !enviando && (
                        confirmacionOk ||
                                estadoPantalla is EstadoSeleccion.YaAsignado ||
                                localSeleccionado != null
                        )

                Button(
                    onClick  = {
                        when {
                            confirmacionOk || estadoPantalla is EstadoSeleccion.YaAsignado -> {
                                onContinuar()
                            }
                            localSeleccionado != null -> {
                                scope.launch {
                                    enviando = true
                                    resultadoMensaje = null
                                    try {
                                        val idLocalSeguro = localSeleccionado?.id ?: ""

                                        val body = BodyCambioSede(
                                            dni      = dni,
                                            local_id = idLocalSeguro
                                        )
                                        val resp = ClienteRed.api.solicitarCambioSede(body)
                                        esExito          = resp.aceptado
                                        resultadoMensaje = resp.mensaje

                                        if (resp.aceptado) {
                                            confirmacionOk = true
                                        }
                                    } catch (e: Exception) {
                                        esExito          = false
                                        resultadoMensaje = "Error en el switchback logístico. Reintenta."
                                    } finally {
                                        enviando = false
                                    }
                                }
                            }
                        }
                    },
                    enabled  = botonHabilitado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(if (botonHabilitado && !enviando) botonScale else 1f),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = if (confirmacionOk) Color(0xFF1DE9B6) else cyanBrillante,
                        disabledContainerColor = cyanBrillante.copy(0.15f)
                    ),
                    shape    = RoundedCornerShape(14.dp)
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            color       = verdeProfundo,
                            modifier    = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text          = textoBoton,
                            color         = verdeProfundo,
                            fontSize      = 14.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }

        // 🔹 DIÁLOGO PARA SELECCIONAR LOCAL (solo si no tiene locales disponibles)
        if (mostrarDialogoMapa) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoMapa = false },
                containerColor = verdeOscuro,
                titleContentColor = Color.White,
                textContentColor = Color.White.copy(0.9f),
                title = { Text("Selecciona tu local de votación") },
                text = {
                    Column {
                        Text("Abre Google Maps para encontrar colegios o locales electorales cerca de ti:")
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                                intent.data = android.net.Uri.parse("geo:0,0?q=colegio electoral cerca de mí")
                                intent.setPackage("com.google.android.apps.maps")
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    context.startActivity(
                                        android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse("https://maps.google.com/?q=colegio electoral")
                                        )
                                    )
                                }
                                mostrarDialogoMapa = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = cyanBrillante)
                        ) {
                            Text("Abrir Google Maps", color = verdeProfundo, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { mostrarDialogoMapa = false }) {
                        Text("Cerrar", color = cyanBrillante)
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SUB-COMPOSABLES INTERNOS COMPLETAMENTE SINKRONIZADOS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CargandoLocales(cyanBrillante: Color, alfaPulso: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(TamanosAdaptativos.tamanoIconoCircular())) {
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .background(
                        Brush.radialGradient(listOf(cyanBrillante.copy(alfaPulso * 0.12f), Color.Transparent)),
                        CircleShape
                    )
            )
            Box(modifier = Modifier.size(TamanosAdaptativos.tamanoIconoCircular() * 0.8f).border(1.5.dp, cyanBrillante.copy(alfaPulso * 0.5f), CircleShape))
            Text("📍", fontSize = 36.sp)
        }
        Spacer(Modifier.height(20.dp))
        Surface(shape = RoundedCornerShape(6.dp), color = cyanBrillante.copy(0.15f)) {
            Row(
                modifier              = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(Modifier.size(5.dp).background(cyanBrillante, CircleShape))
                Spacer(Modifier.width(6.dp))
                Text(
                    "CONSULTANDO PADRÓN",
                    color         = cyanBrillante,
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            "Buscando locales habilitados\nen tu distrito legal.",
            color       = Color.White.copy(0.6f),
            fontSize    = 13.sp,
            textAlign   = TextAlign.Center,
            lineHeight  = 20.sp
        )
    }
}

@Composable
private fun LocalYaAsignado(cyanBrillante: Color, aroAlpha: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(TamanosAdaptativos.tamanoIconoCircular())) {
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .background(
                        Brush.radialGradient(listOf(cyanBrillante.copy(aroAlpha * 0.12f), Color.Transparent)),
                        CircleShape
                    )
            )
            Box(modifier = Modifier.size(88.dp).border(1.5.dp, cyanBrillante.copy(aroAlpha), CircleShape))
            Text("🏫", fontSize = 38.sp)
        }
        Spacer(Modifier.height(18.dp))
        Text("LOCAL", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Text("YA ASIGNADO", color = cyanBrillante, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            "Tu local de votación y mesa ya han sido\nconsolidados en el padrón electoral.",
            color      = Color.White.copy(0.6f),
            fontSize   = 13.sp,
            textAlign  = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cyanBrillante.copy(0.1f))
                .border(1.dp, cyanBrillante.copy(0.35f), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(cyanBrillante.copy(0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("✅", fontSize = 16.sp) }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Tu registro ya está asegurado. Presiona continuar para validar los siguientes pasos.",
                    color      = Color.White.copy(0.8f),
                    fontSize   = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun MensajeError(mensaje: String, cyanBrillante: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        // Icono de error más grande y con fondo
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFF6B6B).copy(0.2f), Color.Transparent)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("⚠️", fontSize = 64.sp)
        }

        EspacioGrande()

        Text(
            "REQUERIMIENTO",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Text(
            "LOGÍSTICO",
            color = Color(0xFFFF6B6B),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        EspacioMedio()

        Text(
            mensaje,
            color = Color.White.copy(0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        EspacioExtraGrande()

        // Botón de reintentar
        Button(
            onClick = { /* TODO: Reintentar conexión */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = cyanBrillante
            )
        ) {
            Text(
                "🔄 REINTENTAR CONEXIÓN",
                color = Color(0xFF00251A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
        }

        EspacioMedio()

        Text(
            "¿Sigues teniendo problemas? Ver ayuda",
            color = Color.White.copy(0.4f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { /* TODO: Abrir ayuda */ }
        )
    }
}

@Composable
private fun ListaLocales(
    locales           : List<LocalVotacionItem>,
    localSeleccionado : LocalVotacionItem?,
    cyanBrillante     : Color,
    accentVerde       : Color,
    resultadoMensaje  : String?,
    esExito           : Boolean,
    onSeleccionar     : (LocalVotacionItem) -> Unit
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(shape = RoundedCornerShape(6.dp), color = cyanBrillante.copy(0.15f)) {
            Row(
                modifier          = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(5.dp).background(cyanBrillante, CircleShape))
                Spacer(Modifier.width(6.dp))
                Text(
                    "PASO 2 DE 4",
                    color         = cyanBrillante,
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "SELECCIONA TU SEDE",
            color         = Color.White,
            fontSize      = 24.sp,
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            textAlign     = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Te asignaremos una mesa en caliente en el instante.",
            color      = Color.White.copy(0.6f),
            fontSize   = 13.sp,
            textAlign  = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier            = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(locales, key = { _, local -> local.id }) { index, local ->
                val esEste = localSeleccionado?.id == local.id

                val cardBg = if (esEste)
                    Brush.horizontalGradient(listOf(cyanBrillante.copy(0.18f), accentVerde.copy(0.12f)))
                else
                    Brush.horizontalGradient(listOf(Color.White.copy(0.07f), Color.White.copy(0.04f)))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .border(
                            width = if (esEste) 1.5.dp else 0.5.dp,
                            color = if (esEste) cyanBrillante.copy(0.7f) else Color.White.copy(0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSeleccionar(local) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (esEste) cyanBrillante.copy(0.25f) else Color.White.copy(0.08f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = if (esEste) "✓" else "${index + 1}",
                            color      = if (esEste) cyanBrillante else Color.White.copy(0.5f),
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = local.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp,
                            color      = if (esEste) Color.White else Color.White.copy(0.85f)
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📍", fontSize = 10.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text     = local.direccion,
                                fontSize = 11.sp,
                                color    = if (esEste) cyanBrillante.copy(0.8f) else Color.White.copy(0.45f),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    RadioButton(
                        selected = esEste,
                        onClick  = { onSeleccionar(local) },
                        colors   = RadioButtonDefaults.colors(
                            selectedColor   = cyanBrillante,
                            unselectedColor = Color.White.copy(0.3f)
                        )
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = resultadoMensaje != null,
            enter   = fadeIn() + slideInVertically { 20 },
            exit    = fadeOut()
        ) {
            Spacer(Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (esExito) Color(0xFF004D40).copy(0.8f) else Color(0xFFB71C1C).copy(0.75f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier          = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (esExito) "✅" else "❌", fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text       = resultadoMensaje ?: "",
                        color      = Color.White,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmacionExitosa(cyanBrillante: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF1DE9B6).copy(0.2f), Color.Transparent)),
                        CircleShape
                    )
            )
            Box(modifier = Modifier.size(95.dp).border(2.dp, Color(0xFF1DE9B6), CircleShape))
            Text("🏫", fontSize = 44.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text("LOCAL", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
        Text("CONFIRMADO", color = cyanBrillante, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tu sede de votación y tu mesa en caliente\nhan sido consolidadas en TECVOTE.",
            color      = Color.White.copy(0.6f),
            fontSize   = 13.sp,
            textAlign  = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cyanBrillante.copy(0.1f))
                .border(1.dp, cyanBrillante.copy(0.4f), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(cyanBrillante.copy(0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("🗳️", fontSize = 16.sp) }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Sede guardada con éxito. Procede al siguiente paso de tu enrolamiento.",
                    color      = Color.White.copy(0.8f),
                    fontSize   = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}