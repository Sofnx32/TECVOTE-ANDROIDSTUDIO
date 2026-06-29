package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
            0.35f to azulOscuro,
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
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logotecvote),
                contentDescription = stringResource(R.string.tecvote),
                modifier = Modifier
                    .size(138.dp)
                    .padding(bottom = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.tecvote),
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.sistema_enrolamiento),
                color = Color.White.copy(0.75f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.onpe_nombre),
                color = cyanBrillante.copy(0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(64.dp))

            Button(
                onClick = onIniciarEnrolamiento,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cyanBrillante)
            ) {
                Text(
                    text = stringResource(R.string.iniciar_enrolamiento),
                    color = azulProfundo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onVerMisDatos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = cyanBrillante)
            ) {
                Text(
                    text = stringResource(R.string.ver_mis_datos),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(52.dp))

            Text(
                text = stringResource(R.string.version_onpe),
                color = Color.White.copy(0.35f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}