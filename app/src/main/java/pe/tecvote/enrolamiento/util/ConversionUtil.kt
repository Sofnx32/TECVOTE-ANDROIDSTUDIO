package pe.tecvote.enrolamiento.util

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

object ConversionUtil {
    fun decodificarBase64AImageBitmap(base64Str: String?): ImageBitmap? {
        if (base64Str.isNullOrBlank()) return null
        return try {
            val bytesLimpios = base64Str.replace("data:image/png;base64,", "").trim()
            val bytesDecodificados = Base64.decode(bytesLimpios, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}