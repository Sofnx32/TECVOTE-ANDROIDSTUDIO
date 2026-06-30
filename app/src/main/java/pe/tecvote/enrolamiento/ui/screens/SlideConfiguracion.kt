package pe.tecvote.enrolamiento.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.data.ClienteRed // 🔹 Importamos tu ClienteRed para cambiar la IP
import pe.tecvote.enrolamiento.ui.EspacioGrande
import pe.tecvote.enrolamiento.ui.EspacioMedio
import pe.tecvote.enrolamiento.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideConfiguracion(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val settingsViewModel: SettingsViewModel = viewModel()

    LaunchedEffect(Unit) {
        settingsViewModel.init(context)
    }

    val azulProfundo = Color(0xFF020B18)
    val azulOscuro = Color(0xFF041529)
    val azulMedio = Color(0xFF0A2547)
    val cyanBrillante = Color(0xFF00C8FF)
    val moradoBoton = Color(0xFF6A5ACD) // 🔹 Color aproximado para el botón "GUARDAR IP"

    val degradeFondo = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to azulMedio,
            0.4f to azulOscuro,
            1.0f to azulProfundo
        )
    )

    val language by settingsViewModel.language.collectAsState()
    val fontSize by settingsViewModel.fontSize.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showConfirmLogoutDialog by remember { mutableStateOf(false) }

    // 🔹 Estados nuevos para manejar la IP dinámica del servidor/ESP32
    var showIpConfigDialog by remember { mutableStateOf(false) }
    var ipTextoInput by remember { mutableStateOf("192.168.1.3:8000") } // Valor inicial sugerido
    var ipActualMostrada by remember { mutableStateOf("192.168.1.3:8000") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.configuracion),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.volver),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(degradeFondo)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            EspacioMedio()

            Text(
                text = "PREFERENCIAS GENERALES",
                color = cyanBrillante.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            ConfiguracionItem(
                icono = Icons.Default.Language,
                titulo = stringResource(R.string.idioma),
                descripcion = getLanguageText(language),
                valorActual = settingsViewModel.getLanguageName(language),
                onClick = { showLanguageDialog = true },
                cyanBrillante = cyanBrillante
            )

            Spacer(Modifier.height(12.dp))

            ConfiguracionItem(
                icono = Icons.Default.TextFields,
                titulo = stringResource(R.string.tamano_letra),
                descripcion = stringResource(R.string.tamano_actual) + getFontSizeText(fontSize),
                onClick = { showFontSizeDialog = true },
                cyanBrillante = cyanBrillante,
                valorActual = "${(fontSize * 100).toInt()}%"
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "SISTEMA Y SEGURIDAD",
                color = cyanBrillante.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            ConfiguracionItem(
                icono = Icons.Default.Accessibility,
                titulo = stringResource(R.string.accesibilidad),
                descripcion = stringResource(R.string.desc_accesibilidad),
                onClick = { showAccessibilityDialog = true },
                cyanBrillante = cyanBrillante
            )

            Spacer(Modifier.height(12.dp))

            ConfiguracionItem(
                icono = Icons.Default.Security,
                titulo = stringResource(R.string.privacidad),
                descripcion = stringResource(R.string.desc_privacidad),
                onClick = { showPrivacyDialog = true },
                cyanBrillante = cyanBrillante
            )

            Spacer(Modifier.height(12.dp))

            ConfiguracionItem(
                icono = Icons.Default.Info,
                titulo = stringResource(R.string.acerca_de),
                descripcion = "Versión 2.0.1 - ONPE 2026",
                onClick = { showAboutDialog = true },
                cyanBrillante = cyanBrillante
            )

            // 🔹 NUEVA SECCIÓN: CONFIGURACIÓN DE RED LOCAL / ESP32-CAM
            Spacer(Modifier.height(24.dp))

            Text(
                text = "DISPOSITIVO EXTERNO (ESP32-CAM)",
                color = cyanBrillante.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            ConfiguracionItem(
                icono = Icons.Default.SettingsRemote,
                titulo = "Dirección IP del Servidor",
                descripcion = "Configura el destino de la API local de Django",
                valorActual = ipActualMostrada,
                onClick = { showIpConfigDialog = true },
                cyanBrillante = cyanBrillante
            )

            EspacioGrande()

            OutlinedButton(
                onClick = { showConfirmLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF4D4D)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF4D4D).copy(alpha = 0.7f), Color(0xFFFF4D4D).copy(alpha = 0.3f))
                    )
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color(0xFFFF4D4D)
                )
                Text(
                    stringResource(R.string.cerrar_sesion),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }

            EspacioGrande()
        }
    }

    val radioColors = RadioButtonDefaults.colors(
        selectedColor = cyanBrillante,
        unselectedColor = Color.White.copy(alpha = 0.4f)
    )

    // 🔹 NUEVO DIÁLOGO: Configuración Dinámica de la IP para Django/ESP32
    if (showIpConfigDialog) {
        AlertDialog(
            onDismissRequest = { showIpConfigDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeveloperMode, contentDescription = null, tint = cyanBrillante)
                    Spacer(Modifier.width(8.dp))
                    Text("Configuración IP", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ingrese la dirección IP local de su servidor Django (con su puerto correspondiente):",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = ipTextoInput,
                        onValueChange = { ipTextoInput = it },
                        label = { Text("Dirección IP del ESP32 / Django", color = Color.White.copy(alpha = 0.5f)) },
                        placeholder = { Text("Ej: 192.168.1.3:8000") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cyanBrillante,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedLabelColor = cyanBrillante,
                            cursorColor = cyanBrillante,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    // Botón estilizado que imita tu diseño nativo "GUARDAR IP"
                    Button(
                        onClick = {
                            if (ipTextoInput.trim().isNotEmpty()) {
                                // ⚡ LLAMADA CRUCIAL AL CLIENTERED MODIFICADO DINÁMICAMENTE ⚡
                                ClienteRed.actualizarBaseUrl(ipTextoInput)
                                ipActualMostrada = ipTextoInput.trim()
                                showIpConfigDialog = false
                                Toast.makeText(context, "IP de red local actualizada!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "La IP no puede estar vacía", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = moradoBoton),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("GUARDAR IP", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIpConfigDialog = false }) {
                    Text("Cancelar", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(stringResource(R.string.seleccionar_idioma), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column {
                    listOf("es" to "Español", "en" to "English", "qu" to "Runa Simi (Quechua)").forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsViewModel.setLanguage(code)
                                    showLanguageDialog = false
                                    activity?.recreate()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = language == code,
                                onClick = {
                                    settingsViewModel.setLanguage(code)
                                    showLanguageDialog = false
                                    activity?.recreate()
                                },
                                colors = radioColors
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(name, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(stringResource(R.string.tamano_letra), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column {
                    listOf(
                        0.8f to "80% - Pequeña",
                        1.0f to "100% - Normal",
                        1.2f to "120% - Grande",
                        1.5f to "150% - Muy Grande"
                    ).forEach { (size, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsViewModel.setFontSize(size)
                                    showFontSizeDialog = false
                                    activity?.recreate()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = fontSize == size,
                                onClick = {
                                    settingsViewModel.setFontSize(size)
                                    showFontSizeDialog = false
                                    activity?.recreate()
                                },
                                colors = radioColors
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(label, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("TecVote", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = cyanBrillante) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.desc_sistema), fontSize = 14.sp, lineHeight = 20.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(Modifier.height(6.dp))
                    Text("Versión: 2.0.1", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("© 2026 ONPE", fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 6.dp))
                    Text("Desarrollado por la Oficina Nacional de Procesos Electorales", fontSize = 12.sp, color = cyanBrillante.copy(alpha = 0.7f), lineHeight = 16.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(stringResource(R.string.accesibilidad), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column {
                    Text(stringResource(R.string.desc_accesibilidad_completo), fontSize = 14.sp, lineHeight = 20.sp, color = Color.White.copy(alpha = 0.85f))
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccessibilityDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(stringResource(R.string.privacidad), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column {
                    Text(stringResource(R.string.desc_privacidad_completo), fontSize = 14.sp, lineHeight = 20.sp, color = Color.White.copy(alpha = 0.85f))
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (showConfirmLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmLogoutDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(stringResource(R.string.confirmar_cierre), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Text(stringResource(R.string.mensaje_cierre), fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
            },
            dismissButton = {
                TextButton(onClick = { showConfirmLogoutDialog = false }) {
                    Text(stringResource(R.string.cancelar), color = Color.White.copy(alpha = 0.6f))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmLogoutDialog = false
                    onLogout()
                }) {
                    Text(stringResource(R.string.cerrar_sesion), color = Color(0xFFFF4D4D), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

@Composable
private fun ConfiguracionItem(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit,
    cyanBrillante: Color,
    valorActual: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(cyanBrillante.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = cyanBrillante,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = Color.White.copy(0.55f),
                    lineHeight = 16.sp
                )
            }

            if (valorActual != null) {
                Text(
                    text = valorActual,
                    color = cyanBrillante,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getLanguageText(lang: String): String {
    return when (lang) {
        "es" -> "Español"
        "en" -> "English"
        "qu" -> "Runa Simi"
        else -> "Español"
    }
}

private fun getFontSizeText(size: Float): String {
    return when {
        size <= 0.85f -> "Pequeña"
        size <= 1.05f -> "Normal"
        size <= 1.3f -> "Grande"
        else -> "Muy Grande"
    }
}