package ru.boilercalc.app.feature.heatcalc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.boilercalc.app.core.model.Building
import ru.boilercalc.app.core.util.Formatting

private val buildingTypes = listOf(
    "Производственное" to 0.5,
    "Административное" to 0.38,
    "Жилое" to 0.45,
    "Складское" to 0.35
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingCard(
    building: Building,
    displayIndex: Int,
    canRemove: Boolean,
    onUpdate: (Building) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var typeExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row: title + remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Здание #$displayIndex",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (canRemove) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить здание",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Building type dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = building.type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Тип здания") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    buildingTypes.forEach { (typeName, q0Value) ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(typeName)
                                    Text(
                                        text = "q\u2080 = $q0Value",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onUpdate(building.copy(type = typeName, q0 = q0Value))
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // q0 display
            Text(
                text = "q\u2080 = ${building.q0} \u0412\u0442/(\u043C\u00B3\u00B7\u00B0C)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Manual volume switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Указать объём вручную",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = building.useManualVolume,
                    onCheckedChange = { onUpdate(building.copy(useManualVolume = it)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (building.useManualVolume) {
                // Manual volume input
                OutlinedTextField(
                    value = if (building.volume > 0) building.volume.toBigDecimal().stripTrailingZeros().toPlainString() else "",
                    onValueChange = { text ->
                        val vol = text.replace(",", ".").toDoubleOrNull() ?: 0.0
                        onUpdate(building.copy(volume = vol))
                    },
                    label = { Text("Объём (м\u00B3)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            } else {
                // Dimension inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (building.width > 0) building.width.toBigDecimal().stripTrailingZeros().toPlainString() else "",
                        onValueChange = { text ->
                            val w = text.replace(",", ".").toDoubleOrNull() ?: 0.0
                            onUpdate(building.copy(width = w))
                        },
                        label = { Text("Ширина (м)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = if (building.length > 0) building.length.toBigDecimal().stripTrailingZeros().toPlainString() else "",
                        onValueChange = { text ->
                            val l = text.replace(",", ".").toDoubleOrNull() ?: 0.0
                            onUpdate(building.copy(length = l))
                        },
                        label = { Text("Длина (м)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = if (building.height > 0) building.height.toBigDecimal().stripTrailingZeros().toPlainString() else "",
                        onValueChange = { text ->
                            val h = text.replace(",", ".").toDoubleOrNull() ?: 0.0
                            onUpdate(building.copy(height = h))
                        },
                        label = { Text("Высота (м)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Computed volume display
                val computedVolume = building.width * building.length * building.height
                if (computedVolume > 0) {
                    Text(
                        text = "V = ${Formatting.formatNumber(computedVolume, 1)} м\u00B3",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
