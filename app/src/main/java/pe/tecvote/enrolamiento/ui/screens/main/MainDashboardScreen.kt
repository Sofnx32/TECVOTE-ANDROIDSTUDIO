package pe.tecvote.enrolamiento.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.tecvote.enrolamiento.ui.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun decodificarQrBase64(base64String: String?): Bitmap? {
    if (base64String.isNullOrBlank()) return null
    return try {
        val bytesDecodificados = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
    } catch (e: Exception) {
        null
    }
}


@Composable
fun MainDashboardScreen(
    dniElector: String,
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Carga automática de datos al montar la pantalla principal
    LaunchedEffect(dniElector) {
        viewModel.cargarDatosDashboard(dniElector)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = TamanosAdaptativos.paddingHorizontalPantalla())
            .verticalScroll(scrollState) // Previene desbordamientos si la tablet es pequeña
    ) {
        when (val currentState = state) {
            is MainState.Cargando -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is MainState.Exito -> {
                val datos = currentState.datos

                // Extraemos los diccionarios mapeados que nos devolvió tu ClienteRed corregido
                val elector = datos.elector ?: emptyMap()
                val mesa = datos.mesa ?: emptyMap()
                val local = datos.localVotacion ?: emptyMap()
                val miembro = datos.miembroMesa ?: emptyMap()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Espaciados.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Encabezado de Bienvenida
                    Text(
                        text = "¡ENROLAMIENTO COMPLETADO!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    EspacioPequeno()

                    Text(
                        text = "${elector["nombre_completo"] ?: "CIUDADANO"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "DNI: $dniElector",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    EspacioGrande()

                    // 2. Tarjeta de Condición Especial: ¿Es Miembro de Mesa?
                    if (miembro["es_miembro"] == true) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(Espaciados.md)) {
                                Text(
                                    text = "📢 ASIGNACIÓN: MIEMBRO DE MESA",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                EspacioPequeno()
                                Text(text = "Cargo: ${miembro["cargo"] ?: "Miembro"}")
                                Text(text = "Horario de instalación: ${miembro["horario"] ?: "06:00 AM"}")
                            }
                        }
                        EspacioMedio()
                    }

                    // 3. Tarjeta Principal de Logística Electoral
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Espaciados.md)) {
                            Text(
                                text = "📍 Tu Información de Votación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = Espaciados.sm))

                            Text(
                                text = "Local: ${local["nombre"] ?: "No asignado aún"}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Dirección: ${local["direccion"] ?: "N/A"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            EspacioMedio()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Mesa", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        text = "${mesa["codigo"] ?: "PENDIENTE"}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text(text = "Aula", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        text = "${mesa["aula"] ?: "101"}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text(text = "Piso", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        text = "${mesa["piso"] ?: "1"}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    EspacioGrande()

                    // 4. Bloque del QR de Constancia Digital Oficial
                    Text(
                        text = "Constancia de Registro Biométrico",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    EspacioPequeno()

                    // Decodificamos dinámicamente la imagen mandada por Django
                    val qrBitmap = remember(datos.qrBase64) {
                        decodificarQrBase64(datos.qrBase64)
                    }

                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Código QR de Constancia",
                            modifier = Modifier
                                .size(TamanosAdaptativos.tamanoLogoPrincipal()) // 🔹 Usa tu tamaño adaptativo inteligente
                                .padding(Espaciados.sm)
                        )
                    } else {
                        // Fallback por si acaso falló el buffer
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    }

                    Text(
                        text = "${datos.codigoConstancia ?: ""}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = "Presenta este código QR el día de las elecciones para agilizar tu acceso a la mesa.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Espaciados.md)
                    )
                }
            }

            is MainState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚠️",
                        style = MaterialTheme.typography.displayMedium
                    )
                    EspacioMedio()
                    Text(
                        text = currentState.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}