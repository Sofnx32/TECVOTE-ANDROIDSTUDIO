package pe.tecvote.enrolamiento.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Funciones helper para espaciado vertical consistente.
 *
 * CONCEPTO ACADÉMICO:
 * - En lugar de escribir Spacer(Modifier.height(16.dp)) en todos lados,
 *   creamos funciones semánticas que describen el PROPÓSITO del espacio.
 * - Esto mejora la legibilidad y mantiene consistencia.
 */

@Composable
fun EspacioPequeno() {
    Spacer(modifier = Modifier.height(Espaciados.sm))
}

@Composable
fun EspacioMedio() {
    Spacer(modifier = Modifier.height(Espaciados.md))
}

@Composable
fun EspacioGrande() {
    Spacer(modifier = Modifier.height(Espaciados.lg))
}

@Composable
fun EspacioExtraGrande() {
    Spacer(modifier = Modifier.height(Espaciados.xl))
}

@Composable
fun EspacioEnorme() {
    Spacer(modifier = Modifier.height(Espaciados.xxl))
}

@Composable
fun EspacioHorizontalPequeno() {
    Spacer(modifier = Modifier.width(Espaciados.sm))
}

@Composable
fun EspacioHorizontalMedio() {
    Spacer(modifier = Modifier.width(Espaciados.md))
}