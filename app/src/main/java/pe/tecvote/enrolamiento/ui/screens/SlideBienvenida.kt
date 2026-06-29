package pe.tecvote.enrolamiento.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.tecvote.enrolamiento.R

@Composable
fun SlideBienvenida(
    modifier: Modifier = Modifier,
    onContinuar: () -> Unit = {}
) {
    val cyanBrillante = Color(0xFF00C8FF)
    val cyanOscuro = Color(0xFF0090CC)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1100, easing = LinearOutSlowInEasing)
    )

    val slideY by animateDpAsState(
        targetValue = if (visible) 0.dp else 40.dp,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing)
    )

    val inf = rememberInfiniteTransition()

    val logoScale by inf.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse)
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(380.dp)
                .offset(x = (-90).dp, y = (-70).dp)
                .background(Brush.radialGradient(listOf(cyanBrillante.copy(0.07f), Color.Transparent)), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .offset(y = slideY)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(56.dp))

            Box(
                modifier = Modifier.size(210.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logotecvote),
                    contentDescription = "Logo TecVote",
                    modifier = Modifier
                        .size(185.dp)
                        .scale(logoScale)
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.bienvenidos_a),
                color = Color.White.copy(0.75f),
                fontSize = 15.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "TECVOTE",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Text(
                text = "SU VOTO SEGURO Y DIGITAL",
                color = cyanBrillante,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(60.dp))

            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(62.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(listOf(cyanOscuro, cyanBrillante)),
                            shape = RoundedCornerShape(50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "COMENZAR",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            PuntosIndicadores(total = 6, actual = 0)
        }
    }
}