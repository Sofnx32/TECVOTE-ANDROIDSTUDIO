package pe.tecvote.enrolamiento.data

import android.content.Context
import android.provider.Settings
import android.util.Base64
import android.util.Log
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

// --- Tus Data Classes se mantienen Exactamente Igual ---

data class RespuestaElector(
    val existe: Boolean,
    val apto: Boolean,
    val nombre: String?,
    val estado_enrolamiento: String?,
    val estado_logistica: String?,
    val ubigeo_legal: String?,
    val mensaje: String
)

data class RespuestaPreguntas(val valido: Boolean, val mensaje: String?)

data class RespuestaBiometria(
    val valido: Boolean,
    val mensaje: String?,
    val confidence: Double? = null,
    val quality_score: Double? = null,
    val ya_enrolado: Boolean? = null,
    val error_code: String? = null,
    val match_score: Double? = null,
    val distrito_asignado: String? = null,
    val token: String? = null
)

data class RespuestaLugarVotacion(
    val estado_logistica: String?,
    val mensaje: String?,
    val nombre_local: String?,
    val direccion: String?,
    val distrito: String?,
    val ubigeo_legal: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val locales_para_elegir: List<LocalItem>? = null
)

data class LocalItem(
    val id: String,
    val nombre: String,
    val direccion: String?,
    val latitud: Double?,
    val longitud: Double?
)

data class RespuestaCambioSede(val aceptado: Boolean, val mensaje: String)
data class RespuestaGuardar(val exitoso: Boolean, val codigo_constancia: String, val mensaje: String?, val token: String? = null, val nombre: String? = null)
data class RespuestaMiMesa(val estado_logistica: String?, val mensaje: String?, val numero_mesa: String?, val piso: String?, val aula: String?)
data class BodyPreguntas(val dni: String, val nombre_padre: String, val nombre_madre: String, val fecha_nacimiento: String)
data class BodyCambioSede(val dni: String, val local_id: String)
data class BodyGuardar(val dni: String, @SerializedName("imageBase64") val imageBase64: String = "")

data class LocalItemResponse(
    val id: String,
    val nombre: String,
    val direccion: String,
    val latitud: Double?,
    val longitud: Double?,
    val distrito: String?,
    val provincia: String?,
    val departamento: String?,
    val ubigeo_distrito: String?,
    val distancia_km: Double?
)

data class RespuestaLocalesCercanos(val status: String, val centro: Map<String, Double>?, val radio_km: Double?, val total: Int, val locales: List<LocalItemResponse>)
data class RespuestaGuardarPreferencia(val exitoso: Boolean, val mensaje: String?, val local: Map<String, String>?)
data class BodyPreferenciaLocal(val dni: String, val local_id: String)


// --- Tu Interfaz de API se mantiene Igual ---

interface TecvoteApi {
    @GET("api/v1/elector/{dni_buscado}/")
    suspend fun buscarElector(@Path("dni_buscado") dni: String): RespuestaElector

    @POST("api/validar-preguntas/")
    suspend fun validarPreguntas(@Body body: BodyPreguntas): RespuestaPreguntas

    @GET("api/locales-por-distrito/{dni}/")
    suspend fun getLugarVotacion(@Path("dni") dni: String): RespuestaLugarVotacion

    @POST("api/elegir-localidad-distrito/")
    suspend fun solicitarCambioSede(@Body body: BodyCambioSede): RespuestaCambioSede

    @POST("api/guardar-enrolamiento/")
    suspend fun guardarEnrolamiento(@Body body: BodyGuardar): RespuestaGuardar

    @GET("api/elector/mi-mesa/{dni}/")
    suspend fun getMiMesa(@Path("dni") dni: String): RespuestaMiMesa

    @Multipart
    @POST("api/biometric-enroll/")
    suspend fun enrolarBiometria(@Part("dni") dni: RequestBody, @Part foto: MultipartBody.Part): RespuestaBiometria

    @Multipart
    @POST("api/biometric-validate/")
    suspend fun validarBiometria(@Part("dni") dni: RequestBody, @Part foto: MultipartBody.Part): RespuestaBiometria

    @Multipart
    @POST("api/login/")
    suspend fun loginBiometricoMLKit(@Part("dni") dni: RequestBody, @Part foto: MultipartBody.Part): RespuestaBiometria

    @GET("api/locales/cercanos/")
    suspend fun getLocalesCercanos(@Query("lat") lat: Double, @Query("lng") lng: Double, @Query("radio") radio: Double = 5.0): RespuestaLocalesCercanos

    @GET("api/locales/ubigeo/{codigo}/")
    suspend fun getLocalesPorUbigeo(@Path("codigo") codigo: String): RespuestaLocalesCercanos

    @POST("api/enrolamiento/preferencia/")
    suspend fun guardarPreferenciaLocalApi(@Body body: BodyPreferenciaLocal): RespuestaGuardarPreferencia

    @GET("api/v1/elector/mis-datos/{dni}/")
    suspend fun getMisDatosElector(@Path("dni") dni: String): RespuestaMisDatos
}

// --- NUEVO CLIENTERED TOTALMENTE DINÁMICO ---

object ClienteRed {

    // 1. Convertido a variable modificable con una IP por defecto inicial
    private var baseUrlConfigurada = "http://192.168.1.3:8000/"
    private const val ENABLE_DEBUG_LOGS = true
    private var appContext: Context? = null

    var tokenSesionBearer: String? = null

    fun init(context: Context) {
        appContext = context.applicationContext

        // OPCIONAL: Cargar la última IP guardada en el dispositivo al iniciar la app
        val prefs = context.getSharedPreferences("tecvote_prefs", Context.MODE_PRIVATE)
        prefs.getString("servidor_ip", null)?.let { ipGuardada ->
            actualizarBaseUrl(ipGuardada)
        }
    }

    /**
     * 🔹 FUNCIÓN CLAVE: Actualiza la URL base de manera dinámica desde Compose.
     * Limpia la cadena e invalida la instancia actual de la API.
     */
    fun actualizarBaseUrl(nuevaIp: String) {
        var ipLimpia = nuevaIp.trim()
        if (ipLimpia.isEmpty()) return

        // Aseguramos protocolo http/https
        if (!ipLimpia.startsWith("http://") && !ipLimpia.startsWith("https://")) {
            ipLimpia = "http://$ipLimpia"
        }
        // Aseguramos diagonal al final obligatoria por Retrofit
        if (!ipLimpia.endsWith("/")) {
            ipLimpia = "$ipLimpia/"
        }

        baseUrlConfigurada = ipLimpia
        Log.d("TECVOTE_NET", "Cambio de endpoint global exitoso a: $baseUrlConfigurada")

        // Al setearlo a null, obligamos al getter a reconstruir el objeto Retrofit
        _api = null

        // Guardamos en SharedPreferences de forma persistente para que no se borre al cerrar la app
        appContext?.let { ctx ->
            ctx.getSharedPreferences("tecvote_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("servidor_ip", nuevaIp.trim())
                .apply()
        }
    }

    private fun generateDeviceFingerprint(): String {
        return try {
            val context = appContext ?: return "unknown"
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(androidId.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(hashBytes, Base64.NO_WRAP or Base64.URL_SAFE)
        } catch (e: Exception) {
            Log.e("TECVOTE", "Error fingerprint: ${e.message}")
            "unknown"
        }
    }

    private val biometricInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        val fingerprint = generateDeviceFingerprint()
        val timestamp = System.currentTimeMillis()

        requestBuilder.addHeader("HTTP_X_DEVICE_FINGERPRINT", fingerprint)
        requestBuilder.addHeader("X-Request-Timestamp", timestamp.toString())

        tokenSesionBearer?.let { token ->
            val cabeceraCorrecta = if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
            requestBuilder.addHeader("Authorization", cabeceraCorrecta)
        }
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (ENABLE_DEBUG_LOGS) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(biometricInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS)
        .writeTimeout(50, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // Variable interna de respaldo para el patrón Singleton dinámico
    private var _api: TecvoteApi? = null

    /**
     * 🔹 GETTER DINÁMICO: Cada vez que una pantalla llame a 'ClienteRed.api',
     * si la IP cambió previamente mediante 'actualizarBaseUrl()', se generará
     * un cliente nuevo apuntando al servidor correcto de manera automática.
     */
    val api: TecvoteApi
        get() {
            if (_api == null) {
                _api = Retrofit.Builder()
                    .baseUrl(baseUrlConfigurada)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(TecvoteApi::class.java)
                Log.d("TECVOTE_NET", "Instancia de Retrofit inicializada en: $baseUrlConfigurada")
            }
            return _api!!
        }

    // --- Métodos de llamadas de red se quedan igual utilizando el getter dinámico 'api' ---

    suspend fun loginBiometricoMLKit(dni: String, fotoFile: File): Result<RespuestaBiometria> {
        return try {
            Log.d("TECVOTE_NET", "Enviando login biométrico ML Kit para DNI: $dni")
            val requestFile = fotoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartPart = MultipartBody.Part.createFormData("foto", fotoFile.name, requestFile)
            val requestDni = dni.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.loginBiometricoMLKit(requestDni, multipartPart)

            if (response.valido) {
                response.token?.let {
                    tokenSesionBearer = it
                    Log.d("TECVOTE_NET", "Token guardado: ${it.take(20)}...")
                }
            }
            Result.success(response)
        } catch (e: Exception) {
            Log.e("TECVOTE_NET", "Error en login biométrico: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun consultarPadrónElector(dni: String): Result<RespuestaElector> {
        return try {
            Log.d("TECVOTE_NET", "Consultando padrón para DNI: $dni")
            val response = api.buscarElector(dni)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("TECVOTE_NET", "Error al consultar elector: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun guardarPreferenciaLocal(
        dni: String,
        localId: String,
        token: String?
    ): Result<RespuestaGuardarPreferencia> {
        return try {
            val response = api.guardarPreferenciaLocalApi(BodyPreferenciaLocal(dni, localId))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}