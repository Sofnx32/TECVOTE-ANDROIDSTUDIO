package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.tecvote.enrolamiento.R

@Composable
fun SlideInicio(
    modifier: Modifier = Modifier,
    onIniciarEnrolamiento: () -> Unit = {},
    onVerMisDatos: () -> Unit = {}
) {
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(cyanBrillante.copy(0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🗳️", fontSize = 72.sp)
            }

            Spacer(Modifier.height(32.dp))

            Text(
                stringResource(R.string.tecvote),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                stringResource(R.string.sistema_enrolamiento),
                color = Color.White.copy(0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(R.string.onpe_nombre),
                color = cyanBrillante.copy(0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onIniciarEnrolamiento,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cyanBrillante)
            ) {
                Text(
                    stringResource(R.string.iniciar_enrolamiento),
                    color = azulProfundo,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onVerMisDatos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = cyanBrillante
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(cyanBrillante, cyanBrillante.copy(0.6f))
                    )
                )
            ) {
                Text(
                    stringResource(R.string.ver_mis_datos),
                    color = cyanBrillante,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(48.dp))

            Text(
                stringResource(R.string.version_onpe),
                color = Color.White.copy(0.3f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}