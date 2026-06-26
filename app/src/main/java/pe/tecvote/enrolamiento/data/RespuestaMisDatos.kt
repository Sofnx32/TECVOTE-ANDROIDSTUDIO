package pe.tecvote.enrolamiento.data

import com.google.gson.annotations.SerializedName

data class RespuestaMisDatos(
    @SerializedName("status") val status: String,
    @SerializedName("mensaje_logistica") val mensajeLogistica: String?,
    @SerializedName("elector") val elector: Map<String, Any>?,
    @SerializedName("mesa") val mesa: Map<String, Any>?,
    @SerializedName("local_votacion") val localVotacion: Map<String, Any>?,
    @SerializedName("miembro_mesa") val miembroMesa: Map<String, Any>?,
    @SerializedName("qr_base64") val qrBase64: String?,
    @SerializedName("codigo_constancia") val codigoConstancia: String?
)