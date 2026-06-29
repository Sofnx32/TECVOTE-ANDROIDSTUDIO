package pe.tecvote.enrolamiento.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.data.ClienteRed
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors


private enum class EstadoCara {
    BUSCANDO, CARA_OK, MUCHAS_CARAS, LISTO
}

@Composable
fun SlideBiometrica(
    modifier: Modifier = Modifier,
    dni: String = "",
    onContinuar: (String?) -> Unit = {}
) {
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

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var tieneCamara by remember { mutableStateOf(false) }
    var enviando by remember { mutableStateOf(false) }

    // SOLUCIÓN AL ERROR DE TIPO DE DATO: Ahora acepta nulos
    var mensajeError by remember { mutableStateOf<String?>(null) }

    var fotoValidada by remember { mutableStateOf(false) }
    var estadoCara by remember { mutableStateOf(EstadoCara.BUSCANDO) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var lenteCamara by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }

    val detector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setMinFaceSize(0.25f)
                .build()
        )
    }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing),
        label = "alpha"
    )

    val inf = rememberInfiniteTransition(label = "inf")

    val colorAro = when (estadoCara) {
        EstadoCara.CARA_OK -> cyanBrillante
        EstadoCara.MUCHAS_CARAS -> Color(0xFFF44336)
        else -> Color.White.copy(0.35f)
    }
    val grosorAro = if (estadoCara == EstadoCara.CARA_OK) 3.dp else 1.5.dp

    val pulso by inf.animateFloat(
        initialValue = 1f,
        targetValue = if (estadoCara == EstadoCara.CARA_OK) 1.05f else 1.02f,
        animationSpec = infiniteRepeatable(
            tween(if (estadoCara == EstadoCara.CARA_OK) 700 else 1400),
            RepeatMode.Reverse
        ), label = "pulso"
    )
    val alfaPunto by inf.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "punto"
    )
    val botonScale by inf.animateFloat(
        initialValue = 1f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "boton"
    )
    val aroAlpha by inf.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "aro"
    )

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { ok -> tieneCamara = ok }

    LaunchedEffect(Unit) {
        tieneCamara = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!tieneCamara) permisoLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(lenteCamara, tieneCamara) {
        if (!tieneCamara) return@LaunchedEffect
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            val previewUC = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            val captureUC = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
            imageCapture = captureUC
            val analysisUC = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            analysisUC.setAnalyzer(Executors.newSingleThreadExecutor()) { proxy ->
                @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                val media = proxy.image
                if (media != null) {
                    val img = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
                    detector.process(img)
                        .addOnSuccessListener { faces ->
                            estadoCara = when {
                                faces.isEmpty() -> EstadoCara.BUSCANDO
                                faces.size > 1 -> EstadoCara.MUCHAS_CARAS
                                else -> EstadoCara.CARA_OK
                            }
                        }
                        .addOnCompleteListener { proxy.close() }
                } else proxy.close()
            }
            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.Builder().requireLensFacing(lenteCamara).build(),
                    previewUC, captureUC, analysisUC
                )
            } catch (e: Exception) { Log.e("TECVOTE", "Error cámara", e) }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
    ) {
        Box(
            modifier = Modifier.size(350.dp).offset(x = (-80).dp, y = (-60).dp)
                .background(Brush.radialGradient(listOf(cyanBrillante.copy(0.06f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier.size(250.dp).align(Alignment.BottomEnd).offset(x = 60.dp, y = 60.dp)
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
                    Text(stringResource(R.string.tecvote), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(stringResource(R.string.sistema_enrolamiento_tec), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Text(stringResource(R.string.tecvote_digital), color = cyanBrillante, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                when {
                    fotoValidada -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(115.dp)
                                    .background(Brush.radialGradient(listOf(Color(0xFF4CAF50).copy(0.2f), Color.Transparent)), CircleShape)
                            )
                            Box(modifier = Modifier.size(95.dp).border(2.dp, Color(0xFF4CAF50), CircleShape))
                            Text("✅", fontSize = 44.sp)
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(stringResource(R.string.verificacion_biometrica), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                        Text(stringResource(R.string.verificacion_completada), color = Color(0xFF4CAF50), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.identidad_verificada), color = Color.White.copy(0.6f), fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF4CAF50).copy(0.12f))
                                .border(1.dp, Color(0xFF4CAF50).copy(0.4f), RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).background(Color(0xFF4CAF50).copy(0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text("🛡️", fontSize = 16.sp) }
                                Spacer(Modifier.width(12.dp))
                                Text(stringResource(R.string.identidad_confirmada), color = Color.White.copy(0.8f), fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }

                    !tieneCamara -> {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
                            Box(modifier = Modifier.size(105.dp).background(Brush.radialGradient(listOf(cyanBrillante.copy(aroAlpha * 0.12f), Color.Transparent)), CircleShape))
                            Box(modifier = Modifier.size(88.dp).border(1.5.dp, cyanBrillante.copy(aroAlpha * 0.5f), CircleShape))
                            Text("📷", fontSize = 36.sp)
                        }
                        Spacer(Modifier.height(18.dp))
                        Text(stringResource(R.string.verificacion_biometrica), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        Text(stringResource(R.string.registro_biometrico), color = cyanBrillante, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.acceso_camara), color = Color.White.copy(0.6f), fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 20.sp)
                    }

                    else -> {
                        Surface(shape = RoundedCornerShape(6.dp), color = cyanBrillante.copy(0.15f)) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(5.dp).background(cyanBrillante, CircleShape))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.tecvote), color = cyanBrillante, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(stringResource(R.string.registro_biometrico), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(6.dp))
                        Text(stringResource(R.string.mire_frente), color = Color.White.copy(0.6f), fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 20.sp)
                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(colorAro.copy(alfaPunto), CircleShape))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (estadoCara) {
                                    EstadoCara.BUSCANDO -> stringResource(R.string.buscando_rostro)
                                    EstadoCara.CARA_OK -> stringResource(R.string.rostro_detectado)
                                    EstadoCara.MUCHAS_CARAS -> stringResource(R.string.solo_un_rostro)
                                    else -> ""
                                },
                                color = colorAro, fontSize = 12.sp, fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        Box(
                            modifier = Modifier.size(TamanosAdaptativos.tamanoLogoPrincipal()),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.size((220 * pulso).dp)
                                    .background(
                                        Brush.radialGradient(listOf(colorAro.copy(0.12f), Color.Transparent)),
                                        CircleShape
                                    )
                            )
                            Box(modifier = Modifier.size(TamanosAdaptativos.tamanoLogoPrincipal() * 0.98f).border(grosorAro, colorAro, CircleShape))
                            AndroidView(modifier = Modifier.size(TamanosAdaptativos.tamanoLogoPrincipal() * 0.93f).clip(CircleShape), factory = { previewView })
                            Box(modifier = Modifier.size(1.5.dp, 16.dp).background(Color.White.copy(0.25f)))
                            Box(modifier = Modifier.size(16.dp, 1.5.dp).background(Color.White.copy(0.25f)))
                            IconButton(
                                onClick = {
                                    lenteCamara = if (lenteCamara == CameraSelector.LENS_FACING_FRONT)
                                        CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(6.dp)
                                    .size(36.dp)
                                    .background(azulMedio.copy(0.9f), CircleShape)
                                    .border(1.dp, cyanBrillante.copy(0.4f), CircleShape)
                            ) { Text("🔄", fontSize = 15.sp) }
                        }
                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            InstruccionItem("👁️", stringResource(R.string.mire_fijamente))
                            InstruccionItem("🎯", stringResource(R.string.centrar_rostro))
                            InstruccionItem("💡", stringResource(R.string.buena_iluminacion))
                        }

                        AnimatedVisibility(visible = mensajeError != null, enter = fadeIn(), exit = fadeOut()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFB71C1C).copy(0.85f),
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                            ) {
                                Text(mensajeError ?: "", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                            }
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        if (fotoValidada) {
                            onContinuar(null)
                        } else if (!tieneCamara) {
                            permisoLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            val capturador = imageCapture
                            if (capturador != null && estadoCara == EstadoCara.CARA_OK && !enviando) {
                                enviando = true
                                mensajeError = null

                                ClienteRed.tokenSesionBearer = null

                                val archivoTemporal = File(context.cacheDir, "temp_face_$dni.jpg")
                                val opcionesSalida = ImageCapture.OutputFileOptions.Builder(archivoTemporal).build()

                                capturador.takePicture(
                                    opcionesSalida,
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            scope.launch {
                                                try {
                                                    val fotoOptimizada = comprimirImagenOptimizado(context, archivoTemporal, dni)

                                                    Log.d("TECVOTE_BIO", "📸 Foto capturada - DNI: $dni")
                                                    Log.d("TECVOTE_BIO", "📤 Enviando validación ML Kit (multipart)...")

                                                    val response = ClienteRed.loginBiometricoMLKit(
                                                        dni = dni,
                                                        fotoFile = fotoOptimizada
                                                    ).getOrNull() ?: throw Exception("Fallo en llamada de red")

                                                    if (response.valido) {
                                                        Log.d("TECVOTE_BIO", "✅ Login exitoso - Token: ${response.token?.take(20)}...")

                                                        if (response.token != null) {
                                                            ClienteRed.tokenSesionBearer = response.token
                                                        }
                                                        fotoValidada = true
                                                        estadoCara = EstadoCara.LISTO

                                                        onContinuar(response.token)

                                                    } else {
                                                        Log.e("TECVOTE_BIO", "Error en validación ML Kit: ${response.mensaje}")
                                                        mensajeError = response.mensaje ?: "La validación biométrica falló"
                                                    }

                                                    if (fotoOptimizada.exists() && fotoOptimizada != archivoTemporal) {
                                                        fotoOptimizada.delete()
                                                    }
                                                } catch (e: Exception) {
                                                    // Usando el Context clásico de Android de forma segura dentro del evento onClick
                                                    mensajeError = context.getString(R.string.error_conexion_servidor)
                                                    Log.e("TECVOTE", "Fallo al procesar login biométrico", e)
                                                } finally {
                                                    enviando = false
                                                    if (archivoTemporal.exists()) archivoTemporal.delete()
                                                }
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            enviando = false
                                            mensajeError = "Fallo al capturar foto: ${exception.message}"
                                        }
                                    }
                                )
                            } else if (estadoCara != EstadoCara.CARA_OK) {
                                mensajeError = context.getString(R.string.error_encuadre_rostro)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TamanosAdaptativos.altoProporcional(0.07))
                        .scale(botonScale)
                        .padding(bottom = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fotoValidada) Color(0xFF4CAF50) else cyanBrillante,
                        disabledContainerColor = cyanBrillante.copy(0.4f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !enviando
                ) {
                    if (enviando) {
                        CircularProgressIndicator(color = azulProfundo, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                    } else {
                        Text(
                            text = if (fotoValidada) stringResource(R.string.finalizar_registro) else stringResource(R.string.registrar_rostro),
                            color = azulProfundo,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun InstruccionItem(icono: String, texto: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(0.05f), CircleShape)
                .border(1.dp, Color.White.copy(0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(icono, fontSize = 16.sp) }
        Spacer(Modifier.height(6.dp))
        Text(texto, color = Color.White.copy(0.5f), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 12.sp)
    }
}

private fun comprimirImagenOptimizado(context: Context, archivoOriginal: File, dni: String): File {
    val archivoComprimido = File(context.cacheDir, "ready_face_$dni.jpg")
    try {
        val opcionesLimites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(archivoOriginal.absolutePath, opcionesLimites)

        val maxDimension = 960
        var anchoOriginal = opcionesLimites.outWidth
        var altoOriginal = opcionesLimites.outHeight

        var sampleSize = 1
        while (anchoOriginal / 2 >= maxDimension || altoOriginal / 2 >= maxDimension) {
            anchoOriginal /= 2
            altoOriginal /= 2
            sampleSize *= 2
        }

        val opcionesLectura = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmapOriginal = BitmapFactory.decodeFile(archivoOriginal.absolutePath, opcionesLectura)
            ?: return archivoOriginal

        FileOutputStream(archivoComprimido).use { flujoSalida ->
            bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 70, flujoSalida)
            flujoSalida.flush()
        }

        bitmapOriginal.recycle()
        return archivoComprimido
    } catch (e: Exception) {
        Log.e("TECVOTE", "Error al comprimir imagen: ${e.message}")
        return archivoOriginal
    }
}