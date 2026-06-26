package pe.tecvote.enrolamiento.ui.screens.localidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
                // Consumimos el endpoint de Django
                val response = ClienteRed.api.getLugarVotacion(dni)

                when (response.estado_logistica) {
                    // 🔹 MAPEADO EXACTO: Capturamos el string que configuraste en Django
                    "HABILITADO", "ASIGNADO" -> {
                        if (response.nombre_local != null && response.direccion != null &&
                            response.latitud != null && response.longitud != null) {

                            _state.value = LocalidadState.LocalDetectado(
                                nombreLocal = response.nombre_local,
                                direccion = response.direccion,
                                distrito = response.distrito ?: "Distrito asignado",
                                latitud = response.latitud,
                                longitud = response.longitud
                            )
                        } else {
                            _state.value = LocalidadState.Error("El elector tiene mesa, pero los datos geográficos del local están incompletos en la base de datos.")
                        }
                    }

                    "BUSQUEDA_ACTIVA" -> {
                        _state.value = LocalidadState.PendienteAsignacion(
                            mensaje = response.mensaje ?: "Elige tu local de votación en el padrón de preferencias."
                        )
                    }

                    "PENDIENTE" -> {
                        _state.value = LocalidadState.PendienteAsignacion(
                            mensaje = response.mensaje ?: "Tu local aún está en proceso de asignación."
                        )
                    }

                    "FALLO_CRIPTOGRAFICO" -> {
                        _state.value = LocalidadState.Error(
                            "No se pudo verificar tu ubicación de forma segura por problemas de cifrado."
                        )
                    }

                    else -> {
                        _state.value = LocalidadState.Error(
                            response.mensaje ?: "Estado de logística electoral no identificado."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = LocalidadState.Error(
                    "Error de comunicación. No se pudo conectar con el servidor central de Tecvote."
                )
            }
        }
    }
}