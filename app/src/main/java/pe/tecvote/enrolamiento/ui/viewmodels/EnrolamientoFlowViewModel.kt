package pe.tecvote.enrolamiento.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.ClienteRed
import pe.tecvote.enrolamiento.data.RespuestaElector

class EnrolamientoFlowViewModel : ViewModel() {

    private val _electorActual = MutableStateFlow<RespuestaElector?>(null)
    val electorActual: StateFlow<RespuestaElector?> = _electorActual.asStateFlow()

    private val _dniActual = MutableStateFlow("")
    val dniActual: StateFlow<String> = _dniActual.asStateFlow()

    fun setDni(dni: String) {
        _dniActual.value = dni
    }

    fun consultarElector(dni: String) {
        viewModelScope.launch {
            Log.d("TECVOTE_FLOW", "Consultando elector: $dni")
            try {
                val resultado = ClienteRed.consultarPadrónElector(dni)
                resultado.onSuccess { elector ->
                    Log.d("TECVOTE_FLOW", "Elector encontrado: ${elector.nombre}")
                    _electorActual.value = elector
                }.onFailure { error ->
                    Log.e("TECVOTE_FLOW", "Error al consultar elector: ${error.message}")
                    _electorActual.value = null
                }
            } catch (e: Exception) {
                Log.e("TECVOTE_FLOW", "Excepción al consultar elector: ${e.message}")
                _electorActual.value = null
            }
        }
    }

    fun limpiar() {
        _electorActual.value = null
        _dniActual.value = ""
    }
}

// Factory para poder obtener el mismo ViewModel en diferentes pantallas
class EnrolamientoFlowViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnrolamientoFlowViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EnrolamientoFlowViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}