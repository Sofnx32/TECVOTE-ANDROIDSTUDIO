package pe.tecvote.enrolamiento.data

import com.google.gson.annotations.SerializedName

data class RespuestaMisDatos(
    @SerializedName("status") val status: String,
    @SerializedName("mensaje_logistica") val mensajeLogistica: String?,
    @SerializedName("elector") val elector: ElectorData?,
    @SerializedName("mesa") val mesa: MesaData?,
    @SerializedName("local_votacion") val localVotacion: LocalVotacionData?,
    @SerializedName("miembro_mesa") val miembroMesa: MiembroMesaData?,
    @SerializedName("qr_base64") val qrBase64: String?,
    @SerializedName("codigo_constancia") val codigoConstancia: String?
)

data class ElectorData(
    @SerializedName("dni") val dni: String,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("estado_enrolamiento") val estadoEnrolamiento: String,
    @SerializedName("biometric_enrolled") val biometricEnrolled: Boolean
)

data class MesaData(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("piso") val piso: String,
    @SerializedName("aula") val aula: String
)

data class LocalVotacionData(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("ubigeo") val ubigeo: String,
    @SerializedName("latitud") val latitud: Double?,
    @SerializedName("longitud") val longitud: Double?
)

data class MiembroMesaData(
    @SerializedName("es_miembro") val esMiembro: Boolean,
    @SerializedName("cargo") val cargo: String,
    @SerializedName("horario") val horario: String?
)