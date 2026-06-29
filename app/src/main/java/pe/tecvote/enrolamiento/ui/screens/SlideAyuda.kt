package pe.tecvote.enrolamiento.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import pe.tecvote.enrolamiento.R

@Composable
fun SlideAyuda(modifier: Modifier = Modifier) {
    val azulProfundo = Color(0xFF020B18)
    val azulOscuro = Color(0xFF041529)
    val azulMedio = Color(0xFF0A2547)
    val cyanBrillante = Color(0xFF00C8FF)

    val degradeFondo = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to azulMedio,
            0.4f to azulOscuro,
            1.0f to azulProfundo
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
    ) {
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                stringResource(R.string.ayuda_soporte),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                stringResource(R.string.portal_oficial),
                color = cyanBrillante,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(16.dp))

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.builtInZoomControls = false
                    settings.displayZoomControls = false
                    loadUrl("https://www.gob.pe/onpe")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        )
    }
}