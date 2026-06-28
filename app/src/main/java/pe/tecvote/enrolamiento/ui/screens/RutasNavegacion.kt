package pe.tecvote.enrolamiento.ui.screens

/**
 * Define todas las rutas de navegación de la aplicación.
 */
object RutasNavegacion {
    // Flujo principal de enrolamiento
    const val BIENVENIDA = "bienvenida"
    const val INGRESO_DNI = "ingreso_dni"
    const val PREGUNTAS = "preguntas/{dni}"
    const val BIOMETRICA = "biometrica/{dni}"
    const val SELECCION_LOCALIDAD = "seleccion_localidad/{dni}"  // ← AHORA RECIBE DNI
    const val GESTION_ENROLAMIENTO = "gestion_enrolamiento/{dni}"
    const val MIS_DATOS = "mis_datos/{dni}"

    // Funciones helper para construir rutas con parámetros
    fun preguntas(dni: String) = "preguntas/$dni"
    fun biometrica(dni: String) = "biometrica/$dni"
    fun seleccionLocalidad(dni: String) = "seleccion_localidad/$dni"  // ← NUEVO
    fun gestionEnrolamiento(dni: String) = "gestion_enrolamiento/$dni"
    fun misDatos(dni: String) = "mis_datos/$dni"
}