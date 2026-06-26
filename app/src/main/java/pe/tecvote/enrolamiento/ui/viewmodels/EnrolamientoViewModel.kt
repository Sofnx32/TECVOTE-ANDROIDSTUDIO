package pe.tecvote.enrolamiento.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.ClienteRed
import pe.tecvote.enrolamiento.data.RespuestaElector
import pe.tecvote.enrolamiento.data.BodyGuardar
import pe.tecvote.enrolamiento.data.RespuestaGuardar

class EnrolamientoViewModel : ViewModel() {

    var uiState by mutableStateOf<RespuestaElector?>(null)
        private set

    var errorGuardar by mutableStateOf<String?>(null)

    var enviando by mutableStateOf(false)

    var resultadoGuardar by mutableStateOf<RespuestaGuardar?>(null)

    fun cargarDatosPadrón(dni: String) {
        viewModelScope.launch {
            val resultado = ClienteRed.consultarPadrónElector(dni)

            resultado.onSuccess { datos ->
                uiState = datos
            }.onFailure { excepcion ->
                Log.e("TECVOTE_VM", "Error en interfaz: ${excepcion.message}")
            }
        }
    }

    fun confirmarYGuardar(dni: String) {
        viewModelScope.launch {
            enviando = true
            errorGuardar = null
            try {
                val response = ClienteRed.api.guardarEnrolamiento(BodyGuardar(dni = dni))
                if (response.exitoso) {
                    resultadoGuardar = response
                } else {
                    errorGuardar = "Error: ${response.mensaje}"
                }
            } catch (e: Exception) {
                errorGuardar = "No se pudo conectar con el servidor local (192.168.1.58). Verifica tu conexión."
            } finally {
                enviando = false
            }
        }
    }
}