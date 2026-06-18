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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.tecvote.enrolamiento.R
import pe.tecvote.enrolamiento.ui.Espaciados
import pe.tecvote.enrolamiento.ui.TamanosAdaptativos
import pe.tecvote.enrolamiento.ui.EspacioPequeno
import pe.tecvote.enrolamiento.ui.EspacioMedio
import pe.tecvote.enrolamiento.ui.EspacioGrande
import pe.tecvote.enrolamiento.ui.EspacioExtraGrande
import pe.tecvote.enrolamiento.ui.EspacioEnorme
import pe.tecvote.enrolamiento.ui.EspacioHorizontalPequeno
import pe.tecvote.enrolamiento.ui.EspacioHorizontalMedio
import pe.tecvote.enrolamiento.ui.theme.TECVOTETheme

@Composable
fun SlideBienvenida(modifier: Modifier = Modifier, onContinuar: () -> Unit = {}) {

    // ── Colores exactos del diseño ONPE ──────────────────────────────
    val azulProfundo  = Color(0xFF020B18)
    val azulOscuro    = Color(0xFF041529)
    val azulMedio     = Color(0xFF0A2547)
    val cyanBrillante = Color(0xFF00C8FF)
    val cyanOscuro    = Color(0xFF0090CC)

    val degradeFondo = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to azulMedio,
            0.4f to azulOscuro,
            1.0f to azulProfundo
        )
    )

    // ── Animación de entrada ─────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label         = "alpha"
    )
    val slideY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 30.dp,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing),
        label         = "slideY"
    )

    // ── Animaciones infinitas ────────────────────────────────────────
    val inf = rememberInfiniteTransition(label = "inf")

    // Pulso del logo
    val logoScale by inf.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.04f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label         = "logo"
    )

    // Brillo del aro cyan
    val aroAlpha by inf.animateFloat(
        initialValue  = 0.5f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label         = "aro"
    )

    // Pulso del botón
    val botonScale by inf.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.02f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label         = "boton"
    )

    // ── Layout principal ─────────────────────────────────────────────
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(degradeFondo)
    ) {

        // Círculos decorativos de fondo (efecto tech)
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            cyanBrillante.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            cyanBrillante.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = TamanosAdaptativos.paddingHorizontalPantalla())
                .offset(y = slideY)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ── HEADER: Logo ONPE ────────────────────────────────────
            Spacer(Modifier.height(8.dp))
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(0.06f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder logo ONPE — reemplaza con tu drawable si tienes
                Box(
                    modifier          = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment  = Alignment.Center
                ) {
                    Text("TECVOTE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "OFICINA NACIONAL DE",
                        color      = Color.White,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "PROCESOS ELECTORALES",
                        color      = Color.White.copy(0.8f),
                        fontSize   = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // ── CENTRO: Logo TECVOTE ─────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Aro cyan + logo con efecto glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier.size(220.dp)
                ) {
                    // Glow exterior
                    Box(
                        modifier = Modifier.size(TamanosAdaptativos.tamanoLogoPrincipal() * 0.95f)
                            .scale(logoScale)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(
                                        cyanBrillante.copy(alpha = aroAlpha * 0.2f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                    // Aro punteado cyan
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(logoScale * 0.98f)
                            .background(Color.Transparent, CircleShape)
                            .then(
                                Modifier.background(
                                    brush = Brush.radialGradient(
                                        listOf(Color.Transparent, Color.Transparent)
                                    )
                                )
                            )
                    )
                    // Logo principal
                    Image(
                        painter            = painterResource(id = R.drawable.logotecvote),
                        contentDescription = "Logo TECVOTE",
                        modifier           = Modifier
                            .size(200.dp)
                            .scale(logoScale)
                    )
                }

                Spacer(Modifier.height(20.dp))



                Spacer(Modifier.height(28.dp))

                // Título principal
                Text(
                    text          = "BIENVENIDOS A",
                    color         = Color.White.copy(0.7f),
                    fontSize      = 14.sp,
                    fontWeight    = FontWeight.Medium,
                    letterSpacing = 4.sp,
                    textAlign     = TextAlign.Center
                )
                Text(
                    text          = "TECVOTE",
                    color         = Color.White,
                    fontSize      = 42.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    textAlign     = TextAlign.Center
                )
                Text(
                    text          = "SU VOTO SEGURO Y DIGITAL",
                    color         = cyanBrillante,
                    fontSize      = 13.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign     = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                // Línea divisora con degradé cyan
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, cyanBrillante, Color.Transparent)
                            )
                        )
                )

                Spacer(Modifier.height(20.dp))

            }

            // ── BOTTOM: Botón + footer ───────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón COMENZAR con degradé cyan
                Button(
                    onClick  = onContinuar,
                    shape    = RoundedCornerShape(50.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor   = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TamanosAdaptativos.altoProporcional(0.07))
                        .scale(botonScale)
                ) {
                    Box(
                        modifier          = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(cyanOscuro, cyanBrillante, cyanOscuro)
                                ),
                                shape = RoundedCornerShape(50.dp)
                            ),
                        contentAlignment  = Alignment.Center
                    ) {
                        Text(
                            text          = "COMENZAR",
                            color         = Color.White,
                            fontSize      = 17.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = 3.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))



                Spacer(Modifier.height(12.dp))

                // Puntos indicadores
                PuntosIndicadores(total = 6, actual = 0)

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
