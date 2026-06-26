package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.ClienteRed
import android.graphics.BitmapFactory
import android.util.Base64
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@Composable
fun MisDatosScreen(
    dni: String,
    token: String?,
    onRegresar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var cargando by remember { mutableStateOf(true) }
    var datos by remember { mutableStateOf<`RespuestaMisDatos.kt`?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(dni) {
        scope.launch {
            try {
                val response = ClienteRed.api.getMisDatosElector(
                    dni = dni,
                    token = token?.let { "Bearer $it" }
                )
                if (response.status == "ok") {
                    datos = response
                } else {
                    // ✅ CORREGIDO: mensaje_logistica, no mensaje
                    mensajeError = response.mensaje_logistica ?: "Error al cargar datos"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.localizedMessage}"
            } finally {
                cargando = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRegresar) {
                Text("←", fontSize = 20.sp)
            }
            Text(
                "Mis Datos",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }

        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (mensajeError != null) {
            Text("❌ $mensajeError", color = Color.Red)
        } else if (datos != null) {
            // ✅ CORREGIDO: Acceso a propiedades de data class (no como Map)
            val elector = datos!!.elector
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("👤 ${elector["nombre"]}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("🆔 DNI: ${elector["dni"]}", fontSize = 14.sp)
                    Text("📋 Estado: ${elector["estado"]}", fontSize = 14.sp)
                }
            }

            // Mesa o local preferido
            if (datos!!.mesa != null) {
                val mesa = datos!!.mesa!!
                val local = datos!!.local_votacion

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🪑 Mesa Asignada", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Código: ${mesa["codigo"]}", fontSize = 14.sp)
                        Text("Piso: ${mesa["piso"]}, Aula: ${mesa["aula"]}", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (local != null) {
                            Text("📍 ${local["nombre"]}", fontWeight = FontWeight.Medium)
                            Text(local["direccion"] as? String ?: "", fontSize = 14.sp)
                            Text("${local["distrito"]}, ${local["provincia"]}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else if (datos!!.local_preferido != null) {
                val local = datos!!.local_preferido!!

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🗺️ Local Seleccionado", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(local["nombre"] as? String ?: "", fontWeight = FontWeight.Medium)
                        Text(local["direccion"] as? String ?: "", fontSize = 14.sp)
                        Text("⏳ Pendiente de sorteo de mesa", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                Text(
                    datos!!.mensaje_logistica ?: "Sin información de local",
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray
                )
            }

            // QR de constancia
            if (!datos!!.qr_base64.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎫 Constancia de Enrolamiento", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        // Decodificar Base64 a Bitmap de forma segura
                        val qrBitmap = remember(datos!!.qr_base64) {
                            try {
                                val base64Data = datos!!.qr_base64!!.replace("data:image/png;base64,", "")
                                val qrBytes = Base64.decode(base64Data, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(qrBytes, 0, qrBytes.size)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Código QR de constancia",
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(8.dp)
                                    .background(Color.White)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            datos!!.qr_texto?.let { texto ->
                                Text(
                                    texto,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        } else {
                            Text("QR no disponible", fontSize = 12.sp, color = Color.Gray)
                        }

                        OutlinedButton(onClick = { /* TODO: Generar PDF */ }) {
                            Text("Descargar Constancia (PDF)")
                        }
                    }
                }
            }

            // Mapa: abrir Google Maps externo con Intent
            val localParaMapa = datos!!.local_votacion ?: datos!!.local_preferido
            if (localParaMapa != null) {
                val lat = localParaMapa["latitud"] as? Double
                val lng = localParaMapa["longitud"] as? Double
                val nombre = localParaMapa["nombre"] as? String

                if (lat != null && lng != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🗺️ Ver ubicación", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(nombre ?: "", fontSize = 14.sp)
                            OutlinedButton(
                                onClick = {
                                    val uri = Uri.parse("geo:$lat,$lng?q=${nombre ?: "mi local"}")
                                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Fallback: abrir en navegador
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$lat,$lng"))
                                        )
                                    }
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Abrir Google Maps")
                            }
                        }
                    }
                }
            }
        }
    }
}