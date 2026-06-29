package pe.tecvote.enrolamiento.ui.screens.localidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.data.ClienteRed

sealed class LocalidadState {
    data object Cargando : LocalidadState()

    data class LocalDetectado(
        val nombreLocal: String,
        val direccion: String,
        val distrito: String,
        val latitud: Double,
        val longitud: Double
    ) : LocalidadState()

    data class PendienteAsignacion(val mensajeResId: Int) : LocalidadState()
    data class Error(val mensajeResId: Int) : LocalidadState()
}

class LocalidadViewModel : ViewModel() {

    private val _state = MutableStateFlow<LocalidadState>(LocalidadState.Cargando)
    val state: StateFlow<LocalidadState> = _state.asStateFlow()

    fun cargarLocalidad(dni: String) {
        if (dni.isBlank()) {
            _state.value = LocalidadState.Error(R.string.dni_no_registrado)
            return
        }

        viewModelScope.launch {
            _state.value = LocalidadState.Cargando

            try {
                val response = ClienteRed.api.getLugarVotacion(dni)

                when (response.estado_logistica) {
                    "HABILITADO", "ASIGNADO" -> {
                        if (response.nombre_local != null && response.direccion != null &&
                            response.latitud != null && response.longitud != null) {

                            _state.value = LocalidadState.LocalDetectado(
                                nombreLocal = response.nombre_local,
                                direccion = response.direccion,
                                distrito = response.distrito ?: "",
                                latitud = response.latitud,
                                longitud = response.longitud
                            )
                        } else {
                            _state.value = LocalidadState.Error(R.string.error_sincronizacion)
                        }
                    }

                    "BUSQUEDA_ACTIVA", "PENDIENTE" -> {
                        _state.value = LocalidadState.PendienteAsignacion(R.string.revisa_todo)
                    }

                    "FALLO_CRIPTOGRAFICO" -> {
                        _state.value = LocalidadState.Error(R.string.error_sincronizacion)
                    }

                    else -> {
                        _state.value = LocalidadState.Error(R.string.sin_conexion_tecvote)
                    }
                }
            } catch (e: Exception) {
                _state.value = LocalidadState.Error(R.string.sin_conexion_tecvote)
            }
        }
    }
}