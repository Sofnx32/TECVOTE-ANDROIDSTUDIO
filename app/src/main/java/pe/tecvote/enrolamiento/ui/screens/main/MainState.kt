package pe.tecvote.enrolamiento.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.ClienteRed
import pe.tecvote.enrolamiento.data.RespuestaMisDatos

sealed class MainState {
    data object Cargando : MainState()
    data class Exito(val datos: RespuestaMisDatos) : MainState()
    data class Error(val mensaje: String) : MainState()
}

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Cargando)

    val state: StateFlow<MainState> = _state.asStateFlow()


    fun cargarDatosDashboard(dni: String) {
        viewModelScope.launch {
            _state.value = MainState.Cargando
            try {

                val response = ClienteRed.api.getMisDatosElector(dni = dni)

                if (response.status == "success") {
                    _state.value = MainState.Exito(response)
                } else {
                    _state.value = MainState.Error(
                        response.mensajeLogistica ?: "Error inesperado al procesar el padrón electoral."
                    )
                }
            } catch (e: Exception) {
                _state.value = MainState.Error(
                    "No se pudo establecer comunicación con el servidor central: ${e.localizedMessage}"
                )
            }
        }
    }
}