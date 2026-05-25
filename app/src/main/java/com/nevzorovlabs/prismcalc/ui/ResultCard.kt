package com.nevzorovlabs.prismcalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nevzorovlabs.prismcalc.logic.EyeResult
import com.nevzorovlabs.prismcalc.logic.PrismResult
import com.nevzorovlabs.prismcalc.ui.theme.AccentPurple
import com.nevzorovlabs.prismcalc.ui.theme.AccentTeal
import java.util.Locale

@Composable
fun ResultCard(result: PrismResult) {
    if (!result.anyInput) {
        EmptyResultCard()
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AccentCard(accentColor = AccentTeal) {
            Text(
                text = "Per eye",
                style = MaterialTheme.typography.titleMedium,
                color = AccentTeal,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            EyeLine(label = "Right eye:", eye = result.right)
            Spacer(Modifier.height(4.dp))
            EyeLine(label = "Left eye: ", eye = result.left)
        }

        AccentCard(accentColor = AccentPurple) {
            Text(
                text = "If distributed equally:",
                style = MaterialTheme.typography.titleMedium,
                color = AccentPurple,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            DistributionLine(
                label = "Right:",
                power = result.distribution.rightPower,
                angle = result.distribution.rightAngle
            )
            Spacer(Modifier.height(4.dp))
            DistributionLine(
                label = "Left: ",
                power = result.distribution.leftPower,
                angle = result.distribution.leftAngle
            )
        }
    }
}

@Composable
private fun EmptyResultCard() {
    AccentCard(accentColor = AccentTeal) {
        Text(
            text = "No prism entered",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AccentCard(
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun EyeLine(label: String, eye: EyeResult) {
    val powerStr = formatPower(eye.power)
    val angleStr = formatAngle(eye.angleTabo)
    val base = if (eye.power < 0.01) "No prism" else eye.baseLabel
    Text(
        text = "$label  $powerStr Δ @ $angleStr°  ($base)",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun DistributionLine(label: String, power: Double, angle: Double) {
    val powerStr = formatPower(power)
    val angleStr = formatAngle(angle)
    Text(
        text = "  $label  $powerStr Δ @ $angleStr°",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

private fun formatPower(value: Double): String =
    String.format(Locale.US, "%.2f", value)

private fun formatAngle(value: Double): String =
    String.format(Locale.US, "%.1f", value)
