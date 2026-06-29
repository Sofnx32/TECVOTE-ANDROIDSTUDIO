package pe.tecvote.enrolamiento.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _language = MutableStateFlow("es")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _fontSize = MutableStateFlow(1.0f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private lateinit var prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "tecvote_settings"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_FONT_SIZE = "font_size"
    }


    fun init(context: Context) {
        if (::prefs.isInitialized) return

        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        _language.value = prefs.getString(KEY_LANGUAGE, "es") ?: "es"
        _fontSize.value = prefs.getFloat(KEY_FONT_SIZE, 1.0f)
    }


    fun setLanguage(code: String) {
        _language.value = code
        prefs.edit().putString(KEY_LANGUAGE, code).apply()
    }


    fun setFontSize(size: Float) {
        _fontSize.value = size
        prefs.edit().putFloat(KEY_FONT_SIZE, size).apply()
    }


    fun getLanguageName(code: String): String {
        return when (code) {
            "en" -> "English"
            "qu" -> "Quechua"
            else -> "Español"
        }
    }
}