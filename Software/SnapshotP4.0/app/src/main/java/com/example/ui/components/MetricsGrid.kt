package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppsScriptMetricsResponse
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MetricsGrid(
    metrics: AppsScriptMetricsResponse,
    modifier: Modifier = Modifier
) {
    val total = metrics.total.coerceAtLeast(0)
    val verdePct = if (total > 0) (metrics.verde.toFloat() / total) else 0f
    val amarilloPct = if (total > 0) (metrics.amarillo.toFloat() / total) else 0f
    val rojoPct = if (total > 0) (metrics.rojo.toFloat() / total) else 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MÉTRICAS EN TIEMPO REAL",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                ),
                color = TextSecondary
            )

            metrics.timestamp?.let { ts ->
                Text(
                    text = "Última lectura: $ts",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid 2x2
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Total Cajas
                MetricCard(
                    title = "Total Cajas",
                    subtitle = "Procesadas por Cinta",
                    value = metrics.total.toString(),
                    percentage = null,
                    color = NeonCyan,
                    icon = Icons.Default.Inventory2,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_card_total")
                )

                // Card 2: Buen Estado (Verde)
                MetricCard(
                    title = "Buen Estado",
                    subtitle = "Cajas Aprobadas",
                    value = metrics.verde.toString(),
                    percentage = verdePct,
                    color = NeonGreen,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_card_verde")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 3: Indeterminado (Amarillo)
                MetricCard(
                    title = "Indeterminado",
                    subtitle = "Revisión Manual",
                    value = metrics.amarillo.toString(),
                    percentage = amarilloPct,
                    color = NeonAmber,
                    icon = Icons.Default.Help,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_card_amarillo")
                )

                // Card 4: Mal Estado (Rojo)
                MetricCard(
                    title = "Mal Estado",
                    subtitle = "Cajas Rechazadas",
                    value = metrics.rojo.toString(),
                    percentage = rojoPct,
                    color = NeonRed,
                    icon = Icons.Default.Error,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_card_rojo")
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    subtitle: String,
    value: String,
    percentage: Float?,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage ?: 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        color = SlateCardSurface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                percentage?.let {
                    val pctString = "${(it * 100).toInt()}%"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(color.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = pctString,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = color
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = TextPrimary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = TextSecondary
            )

            percentage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.15f)
                )
            }
        }
    }
}
