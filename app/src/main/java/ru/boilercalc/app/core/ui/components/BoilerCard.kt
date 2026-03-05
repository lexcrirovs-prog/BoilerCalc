package ru.boilercalc.app.core.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.boilercalc.app.core.data.BoilerUrls
import ru.boilercalc.app.core.domain.SelectedSteamBoiler
import ru.boilercalc.app.core.util.Formatting

/**
 * Elevated card displaying a selected steam boiler model with key specs.
 * Shows model name, capacity, gas consumption, and load percentage.
 * Has an expandable section with a link to the product page.
 *
 * @param boiler The selected steam boiler data
 * @param gasConsumptionPerUnit Calculated gas consumption for this specific boiler unit (m3/h)
 * @param pressureBar Current pressure in bar (for URL resolution)
 * @param modifier Optional modifier
 */
@Composable
fun BoilerCard(
    boiler: SelectedSteamBoiler,
    gasConsumptionPerUnit: Double,
    pressureBar: Double,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            // Header row: model name + count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = boiler.model.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (boiler.count > 1) {
                        Text(
                            text = "Количество: ${boiler.count} шт.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Key specs row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecItem(
                    label = "Производительность",
                    value = "${Formatting.formatNumber(boiler.model.maxSteam.toDouble(), 0)} кг/ч"
                )
                SpecItem(
                    label = "Нагрузка",
                    value = "${Formatting.formatNumber(boiler.loadPercent, 1).trimEnd('0').trimEnd(',')}%"
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecItem(
                    label = "Расход газа",
                    value = "${Formatting.formatNumber(gasConsumptionPerUnit, 1).trimEnd('0').trimEnd(',')} м\u00B3/ч"
                )
                SpecItem(
                    label = "Диапазон",
                    value = "${Formatting.formatNumber(boiler.model.minSteam.toDouble(), 0)}–${Formatting.formatNumber(boiler.model.maxSteam.toDouble(), 0)} кг/ч"
                )
            }

            // Expandable section with link
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Resolve URL
                    val pressureKey = if (pressureBar <= 10.0) "8 бар" else "12 бар"
                    val urlPath = BoilerUrls.steamUrls[boiler.model.id]?.get(pressureKey)

                    if (urlPath != null) {
                        val fullUrl = BoilerUrls.BASE_URL + urlPath
                        TextButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Страница на сайте",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Text(
                        text = "Номинальный расход газа (Qн=8484 ккал/м\u00B3): ${boiler.model.gas8484} м\u00B3/ч",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
