package pe.tecvote.enrolamiento.ui.screens

import android.graphics.Bitmap
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Estado para controlar la visibilidad de la barra de progreso
    var estaCargando by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
            .statusBarsPadding()
    ) {
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.ayuda_soporte),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.portal_oficial),
                color = cyanBrillante,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(
            visible = estaCargando,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = cyanBrillante,
                trackColor = azulOscuro
            )
        }

        AndroidView(
            factory = { context ->
                WebView(context).apply {

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            estaCargando = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            estaCargando = false
                        }
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = false
                        displayZoomControls = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }

                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    loadUrl("https://www.gob.pe/onpe")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onRelease = { webView ->
                webView.stopLoading()
                webView.removeAllViews()
                webView.destroy()
            }
        )
    }
}