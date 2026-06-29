package pe.tecvote.enrolamiento

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import pe.tecvote.enrolamiento.ui.screens.MainEnrolamientoFlow
import pe.tecvote.enrolamiento.ui.theme.TecVoteTheme
import pe.tecvote.enrolamiento.viewmodels.SettingsViewModel
import androidx.core.view.WindowCompat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = SettingsViewModel()
        settingsViewModel.init(applicationContext)

        // 🔹 APLICAR IDIOMA GUARDADO AL INICIAR
        val prefs = getSharedPreferences("tecvote_settings", MODE_PRIVATE)
        val language = prefs.getString("language", "es") ?: "es"

        // 🔹 FORZAR LOCALE EN EL CONTEXTO DE LA ACTIVIDAD
        setLocale(language)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val fontSize by settingsViewModel.fontSize.collectAsState()
            val currentLanguage by settingsViewModel.language.collectAsState()

            // 🔹 CREAR CONTEXTO CON EL LOCALE CORRECTO PARA COMPOSE
            val localizedContext = createLocalizedContext(this, currentLanguage)

            TecVoteTheme(fontSizeScale = fontSize) {
                // 🔹 PROVEER EL CONTEXTO LOCALIZADO A TODA LA APP
                CompositionLocalProvider(LocalContext provides localizedContext) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainEnrolamientoFlow(
                            onLogout = {
                                clearSession()
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }

    // 🔹 FUNCIÓN PARA FORZAR EL LOCALE EN LA ACTIVIDAD
    private fun setLocale(languageCode: String) {
        val locale = when (languageCode) {
            "en" -> Locale("en")
            "qu" -> Locale("qu")
            else -> Locale("es")
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    // 🔹 FUNCIÓN PARA CREAR CONTEXTO LOCALIZADO PARA COMPOSE
    private fun createLocalizedContext(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "en" -> Locale("en")
            "qu" -> Locale("qu")
            else -> Locale("es")
        }

        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    private fun clearSession() {
        val prefs = getSharedPreferences("tecvote_session", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val tokenPrefs = getSharedPreferences("tecvote_token", MODE_PRIVATE)
        tokenPrefs.edit().clear().apply()
    }
}