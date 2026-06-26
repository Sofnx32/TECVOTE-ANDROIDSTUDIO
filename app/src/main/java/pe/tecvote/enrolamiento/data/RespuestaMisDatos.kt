package pe.tecvote.enrolamiento.data

import com.google.gson.annotations.SerializedName

data class RespuestaMisDatos(
    @SerializedName("status") val status: String = "success",
    @SerializedName("codigo_constancia") val codigoConstancia: String? = null,
    @SerializedName("qr_base64") val qrBase64: String = "",
    @SerializedName("mensaje_logistica") val mensajeLogistica: String? = null,
    @SerializedName("elector") val elector: ElectorData?,
    @SerializedName("mesa") val mesa: MesaData?,
    @SerializedName("local_votacion") val localVotacion: LocalVotacionData?,
    @SerializedName("miembro_mesa") val miembroMesa: MiembroMesaData? = null
)

data class ElectorData(
    @SerializedName("dni") val dni: String,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("estado") val estado: String, // HABILITADO, ENROLADO, VOTADO
    @SerializedName("ubigeo_domicilio") val ubigeoDomicilio: String,
    @SerializedName("fecha_asistencia") val fechaAsistencia: String? = null
) {
    val biometricEnrolled: Boolean get() = estado == "ENROLADO" || estado == "VOTADO"
    val estadoEnrolamiento: String get() = estado
}

data class MesaData(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("piso") val piso: String = "1",
    @SerializedName("aula") val aula: String = "Aula General",
    @SerializedName("estado") val estado: String
)

data class LocalVotacionData(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("ubigeo") val ubigeo: String,
    @SerializedName("latitud") val latitud: Double?,
    @SerializedName("longitud") val longitud: Double?
)

data class MiembroMesaData(
    @SerializedName("es_miembro") val esMiembro: Boolean = false,
    @SerializedName("cargo") val cargo: String = "TERCER MIEMBRO",
    @SerializedName("horario") val horario: String? = "07:00 AM - 05:00 PM"
)