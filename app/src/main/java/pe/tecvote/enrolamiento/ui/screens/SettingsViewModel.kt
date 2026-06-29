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

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("tecvote_settings", Context.MODE_PRIVATE)
        loadSettings()
    }

    private fun loadSettings() {
        _language.value = prefs?.getString("language", "es") ?: "es"
        _fontSize.value = prefs?.getFloat("font_size", 1.0f) ?: 1.0f
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        prefs?.edit()?.putString("language", lang)?.apply()
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size
        prefs?.edit()?.putFloat("font_size", size)?.apply()
    }

    fun getLanguageName(code: String): String {
        return when (code) {
            "es" -> "Español"
            "en" -> "English"
            "qu" -> "Runa Simi (Quechua)"
            else -> "Español"
        }
    }
}