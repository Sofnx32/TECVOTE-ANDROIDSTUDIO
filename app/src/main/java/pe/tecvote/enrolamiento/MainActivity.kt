    package pe.tecvote.enrolamiento

    import android.content.Context
    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.activity.viewModels
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.runtime.CompositionLocalProvider
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.core.view.WindowCompat
    import pe.tecvote.enrolamiento.ui.screens.MainEnrolamientoFlow
    import pe.tecvote.enrolamiento.ui.theme.TecVoteTheme
    import pe.tecvote.enrolamiento.viewmodels.SettingsViewModel
    import java.util.Locale
    import androidx.activity.compose.LocalActivityResultRegistryOwner
    import androidx.lifecycle.compose.LocalLifecycleOwner
    import androidx.savedstate.compose.LocalSavedStateRegistryOwner

    class MainActivity : ComponentActivity() {

        private val settingsViewModel: SettingsViewModel by viewModels()

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

            super.attachBaseContext(newBase.createConfigurationContext(config))
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            settingsViewModel.init(applicationContext)

            WindowCompat.setDecorFitsSystemWindows(window, false)

            setContent {
                val fontSize by settingsViewModel.fontSize.collectAsState()
                val currentLanguage by settingsViewModel.language.collectAsState()
                val localizedContext = createLocalizedContext(LocalContext.current, currentLanguage)

                TecVoteTheme(fontSizeScale = fontSize) {
                    // ✅ Proporcionamos los "owners" que Navigation Compose necesita
                    CompositionLocalProvider(
                        LocalContext provides localizedContext,
                        androidx.activity.compose.LocalActivityResultRegistryOwner provides this@MainActivity,
                        androidx.lifecycle.compose.LocalLifecycleOwner provides this@MainActivity,
                        androidx.savedstate.compose.LocalSavedStateRegistryOwner provides this@MainActivity
                    ) {
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

        private fun createLocalizedContext(
            context: Context,
            languageCode: String
        ): Context {

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
            getSharedPreferences("tecvote_session", MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            getSharedPreferences("tecvote_token", MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }
    }