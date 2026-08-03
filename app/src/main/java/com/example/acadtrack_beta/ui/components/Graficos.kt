package com.example.acadtrack_beta.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Dona proporcional dibujada a mano: cada valor es un arco cuyo ángulo
// es (cantidad / total) * 360°. Sirve para 3-5 categorías, que es nuestro caso.
@Composable
fun GraficoDona(
    valores: List<Pair<String, Int>>,
    colores: List<Color>,
    modifier: Modifier = Modifier,
    tamano: Dp = 120.dp,
    grosor: Dp = 24.dp
) {
    val total = valores.sumOf { it.second }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Canvas(modifier = Modifier.size(tamano)) {
            if (total == 0) {
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = grosor.toPx(), cap = StrokeCap.Butt)
                )
                return@Canvas
            }
            var anguloInicio = -90f
            valores.forEachIndexed { indice, (_, cantidad) ->
                if (cantidad <= 0) return@forEachIndexed
                val angulo = 360f * cantidad / total
                drawArc(
                    color = colores[indice % colores.size],
                    startAngle = anguloInicio,
                    sweepAngle = angulo,
                    useCenter = false,
                    style = Stroke(width = grosor.toPx(), cap = StrokeCap.Butt)
                )
                anguloInicio += angulo
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            valores.forEachIndexed { indice, (etiqueta, cantidad) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colores[indice % colores.size], CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$etiqueta ($cantidad)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// Barra de progreso con etiqueta a la izquierda y "completadas/total" a la derecha.
@Composable
fun BarraProgreso(
    etiqueta: String,
    completadas: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progreso = if (total == 0) 0f else completadas.toFloat() / total

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$completadas/$total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

