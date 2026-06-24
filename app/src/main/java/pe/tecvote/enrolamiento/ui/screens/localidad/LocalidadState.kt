package pe.tecvote.enrolamiento.ui.screens.localidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.data.ClienteRed
import kotlinx.coroutines.flow.asStateFlow
sealed class LocalidadState {
    object Cargando : LocalidadState()
    data class LocalDetectado(
        val nombreLocal: String,
        val direccion: String,
        val distrito: String,
        val latitud: Double,
        val longitud: Double
    ) : LocalidadState()
    data class PendienteAsignacion(val mensaje: String) : LocalidadState()
    data class Error(val mensaje: String) : LocalidadState()
}

class LocalidadViewModel : ViewModel() {

    private val _state = MutableStateFlow<LocalidadState>(LocalidadState.Cargando)
    val state: StateFlow<LocalidadState> = _state.asStateFlow()

    fun cargarLocalidad(dni: String) {
        if (dni.isBlank()) {
            _state.value = LocalidadState.Error("DNI no proporcionado")
            return
        }

        viewModelScope.launch {
            _state.value = LocalidadState.Cargando

            try {
                val response = ClienteRed.api.getLugarVotacion(dni)

                when (response.estado_logistica) {
                    "ASIGNADO" -> {
                        if (response.nombre_local != null && response.direccion != null) {
                            _state.value = LocalidadState.LocalDetectado(
                                nombreLocal = response.nombre_local,
                                direccion = response.direccion,
                                distrito = response.distrito ?: "Distrito no disponible",
                                latitud = response.latitud ?: -12.046374,
                                longitud = response.longitud ?: -77.042793
                            )
                        } else {
                            _state.value = LocalidadState.Error("Datos del local incompletos")
                        }
                    }
                    "PENDIENTE", "BUSQUEDA_ACTIVA" -> {
                        _state.value = LocalidadState.PendienteAsignacion(
                            mensaje = response.mensaje ?: "Tu local aún está siendo asignado."
                        )
                    }
                    "FALLO_CRIPTOGRAFICO" -> {
                        _state.value = LocalidadState.Error(
                            "No se pudo verificar tu ubicación de forma segura."
                        )
                    }
                    else -> {
                        _state.value = LocalidadState.Error(
                            response.mensaje ?: "Estado desconocido"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = LocalidadState.Error(
                    "Error de conexión. Verifica tu internet e intenta nuevamente."
                )
            }
        }
    }
}