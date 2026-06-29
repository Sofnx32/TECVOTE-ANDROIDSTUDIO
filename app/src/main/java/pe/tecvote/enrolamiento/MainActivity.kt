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
// 🔹 IMPORTACIÓN CORRETA DE TU VIEWMODEL
import androidx.core.view.WindowCompat
import java.util.Locale

class MainActivity : ComponentActivity() {

    // 🔹 CORREGIDO: Se usa el nombre de la clase, sin el ".kt"
    private lateinit var settingsViewModel: SettingsViewModel

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("tecvote_settings", MODE_PRIVATE)
        val languageCode = prefs.getString("language", "es") ?: "es"

        val locale = when (languageCode) {
            "en" -> Locale("en")
            "qu" -> Locale("qu")
            else -> Locale("es")
        }
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val localizedContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = SettingsViewModel()
        settingsViewModel.init(applicationContext)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val fontSize by settingsViewModel.fontSize.collectAsState()
            val currentLanguage by settingsViewModel.language.collectAsState()

            val localizedContext = createLocalizedContext(LocalContext.current, currentLanguage)

            TecVoteTheme(fontSizeScale = fontSize) {
                CompositionLocalProvider(LocalContext provides localizedContext) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background // 🔹 Ajustado de .background a .colorScheme.background si usas M3 estándar
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

    private fun createLocalizedContext(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "en" -> Locale("en")
            "qu" -> Locale("qu")
            else -> Locale("es")
        }

        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    private fun clearSession() {
        val prefs = getSharedPreferences("tecvote_session", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val tokenPrefs = getSharedPreferences("tecvote_token", MODE_PRIVATE)
        tokenPrefs.edit().clear().apply()
    }
}