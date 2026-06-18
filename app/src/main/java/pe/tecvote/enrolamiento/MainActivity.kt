package pe.tecvote.enrolamiento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pe.tecvote.enrolamiento.ui.screens.MainEnrolamientoFlow
import pe.tecvote.enrolamiento.ui.theme.TECVOTETheme

/**
 * Activity principal de la aplicación.
 *
 * CONCEPTO ACADÉMICO:
 * - Activity = Ventana principal donde se dibuja la UI
 * - setContent { } = Define el contenido Compose de la Activity
 * - enableEdgeToEdge() = Permite que la UI se extienda bajo las barras del sistema
 *
 * En arquitectura moderna con Compose:
 * - Solo necesitamos UNA Activity (MainActivity)
 * - Toda la navegación se maneja internamente con NavHost
 * - Esto es más eficiente que crear múltiples Activities
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TECVOTETheme {
                // MainEnrolamientoFlow ahora contiene el NavHost internamente
                MainEnrolamientoFlow()
            }
        }
    }
}