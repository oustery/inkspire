package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PainExtreme
import com.example.ui.theme.PainHigh
import com.example.ui.theme.PainLow
import com.example.ui.theme.PainMedium

@Composable
fun PainMeter(
    level: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    val (color, label) = when {
        level <= 3 -> Pair(PainLow, "Легкая боль (терпимо)")
        level <= 6 -> Pair(PainMedium, "Умеренная боль (дискомфорт)")
        level <= 8 -> Pair(PainHigh, "Ощутимая боль (нужен перерыв)")
        else -> Pair(PainExtreme, "Интенсивная боль (острая зона)")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Шкала чувствительности: $level / 10",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }

        // 10-segment pain indicator bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..10) {
                val isActive = i <= level
                val segColor = when {
                    i <= 3 -> PainLow
                    i <= 6 -> PainMedium
                    i <= 8 -> PainHigh
                    else -> PainExtreme
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isActive) segColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            }
        }

        if (description.isNotEmpty()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
