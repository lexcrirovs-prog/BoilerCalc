package ru.boilercalc.app.feature.heatcalc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.boilercalc.app.core.domain.SelectedWaterBoiler
import ru.boilercalc.app.core.util.Formatting

@Composable
fun HeatResultsSection(
    qHeating: Double,
    qDHW: Double,
    qTotal: Double,
    annualHeat: Double,
    gsop: Double,
    selectedBoilers: List<SelectedWaterBoiler>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Результаты расчёта",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Heat load cards row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeatValueCard(
                title = "Q\u043E\u0442",
                value = Formatting.formatNumber(qHeating, 1),
                unit = "кВт",
                modifier = Modifier.weight(1f)
            )
            HeatValueCard(
                title = "Q\u0433\u0432\u0441",
                value = Formatting.formatNumber(qDHW, 1),
                unit = "кВт",
                modifier = Modifier.weight(1f)
            )
            HeatValueCard(
                title = "Q\u0441\u0443\u043C\u043C",
                value = Formatting.formatNumber(qTotal, 1),
                unit = "кВт",
                modifier = Modifier.weight(1f),
                highlighted = true
            )
        }

        // Annual heat and GSOP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeatValueCard(
                title = "Q\u0433\u043E\u0434",
                value = Formatting.formatNumber(annualHeat, 1),
                unit = "МВт\u00B7ч/год",
                modifier = Modifier.weight(1f)
            )
            HeatValueCard(
                title = "ГСОП",
                value = Formatting.formatNumber(gsop, 0),
                unit = "\u00B0С\u00B7сут",
                modifier = Modifier.weight(1f)
            )
        }

        // Selected boilers
        if (selectedBoilers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Подобранные котлы",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            selectedBoilers.forEach { selected ->
                BoilerResultCard(selected = selected)
            }
        }
    }
}

@Composable
private fun HeatValueCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (highlighted)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (highlighted)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = if (highlighted)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BoilerResultCard(
    selected: SelectedWaterBoiler,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${selected.model.series} - ${selected.model.power}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = if (selected.model.isPremiumE) "Серия E" else "Серия C",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${selected.count} шт.",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "${selected.model.power} кВт",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
