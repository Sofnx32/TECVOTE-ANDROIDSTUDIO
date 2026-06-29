package pe.tecvote.enrolamiento.ui.screens

import android.app.Activity
import android.content.Context
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
import pe.tecvote.enrolamiento.ui.EspacioGrande
import pe.tecvote.enrolamiento.ui.EspacioMedio
import pe.tecvote.enrolamiento.viewmodels.SettingsViewModel
import java.util.Locale

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.configuracion),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.volver), tint = Color.White)
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
                .padding(16.dp)
        ) {
            EspacioMedio()

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

            Spacer(Modifier.height(12.dp))

            ConfiguracionItem(
                icono = Icons.Default.Accessibility,
                titulo = stringResource(R.string.accesibilidad),
                descripcion = stringResource(R.string.desc_accesibilidad),
                onClick = { showAccessibilityDialog = true },
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

            Spacer(Modifier.height(12.dp))

            ConfiguracionItem(
                icono = Icons.Default.Security,
                titulo = stringResource(R.string.privacidad),
                descripcion = stringResource(R.string.desc_privacidad),
                onClick = { showPrivacyDialog = true },
                cyanBrillante = cyanBrillante
            )

            EspacioGrande()

            OutlinedButton(
                onClick = { showConfirmLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Red, Color.Red.copy(0.6f))
                    )
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(stringResource(R.string.cerrar_sesion), fontWeight = FontWeight.Bold)
            }

            EspacioGrande()
        }
    }

    // 🔹 DIÁLOGO DE IDIOMA - AHORA FUNCIONAL
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.seleccionar_idioma), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("es" to "Español", "en" to "English", "qu" to "Runa Simi (Quechua)").forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // 🔹 CAMBIAR IDIOMA Y RECREAR ACTIVIDAD
                                    settingsViewModel.setLanguage(code)
                                    showLanguageDialog = false

                                    // 🔹 FORZAR CAMBIO DE IDIOMA
                                    val locale = when (code) {
                                        "en" -> Locale("en")
                                        "qu" -> Locale("qu")
                                        else -> Locale("es")
                                    }
                                    Locale.setDefault(locale)
                                    val config = context.resources.configuration
                                    config.setLocale(locale)
                                    context.resources.updateConfiguration(config, context.resources.displayMetrics)

                                    // 🔹 RECREAR ACTIVIDAD PARA APLICAR CAMBIOS
                                    activity?.recreate()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = language == code,
                                onClick = {
                                    settingsViewModel.setLanguage(code)
                                    showLanguageDialog = false

                                    val locale = when (code) {
                                        "en" -> Locale("en")
                                        "qu" -> Locale("qu")
                                        else -> Locale("es")
                                    }
                                    Locale.setDefault(locale)
                                    val config = context.resources.configuration
                                    config.setLocale(locale)
                                    context.resources.updateConfiguration(config, context.resources.displayMetrics)

                                    activity?.recreate()
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(name, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // 🔹 DIÁLOGO DE TAMAÑO DE LETRA
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            title = { Text(stringResource(R.string.tamano_letra), fontWeight = FontWeight.Bold) },
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
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = fontSize == size,
                                onClick = {
                                    settingsViewModel.setFontSize(size)
                                    showFontSizeDialog = false
                                    activity?.recreate()
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(label, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // 🔹 DIÁLOGO ACERCA DE
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("TecVote", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            text = {
                Column {
                    Text(stringResource(R.string.desc_sistema), fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Versión: 2.0.1", fontSize = 12.sp)
                    Text("© 2026 ONPE", fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Desarrollado por la Oficina Nacional de Procesos Electorales", fontSize = 11.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // 🔹 DIÁLOGO DE ACCESIBILIDAD
    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text(stringResource(R.string.accesibilidad), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(stringResource(R.string.desc_accesibilidad_completo), fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccessibilityDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // 🔹 DIÁLOGO DE PRIVACIDAD
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.privacidad), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(stringResource(R.string.desc_privacidad_completo), fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(stringResource(R.string.cerrar), color = cyanBrillante)
                }
            },
            containerColor = Color(0xFF041529),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // 🔹 DIÁLOGO DE CONFIRMACIÓN DE CIERRE DE SESIÓN
    if (showConfirmLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmLogoutDialog = false },
            title = { Text(stringResource(R.string.confirmar_cierre), fontWeight = FontWeight.Bold) },
            text = {
                Text(stringResource(R.string.mensaje_cierre), fontSize = 14.sp)
            },
            dismissButton = {
                TextButton(onClick = { showConfirmLogoutDialog = false }) {
                    Text(stringResource(R.string.cancelar), color = Color.White)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmLogoutDialog = false
                    onLogout()
                }) {
                    Text(stringResource(R.string.cerrar_sesion), color = Color.Red)
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
    valorActual: String? = null,
    switchValue: Boolean? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(0.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = cyanBrillante,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = Color.White.copy(0.6f)
                )
            }

            if (valorActual != null) {
                Text(
                    text = valorActual,
                    color = cyanBrillante,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(0.4f)
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