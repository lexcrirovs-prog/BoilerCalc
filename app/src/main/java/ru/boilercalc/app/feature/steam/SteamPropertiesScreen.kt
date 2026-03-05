package ru.boilercalc.app.feature.steam

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.boilercalc.app.core.data.SteamTable
import ru.boilercalc.app.core.domain.SteamCalculationEngine
import ru.boilercalc.app.core.model.LeadFormData
import ru.boilercalc.app.core.network.LeadRepository
import ru.boilercalc.app.core.ui.components.BoilerCard
import ru.boilercalc.app.core.ui.components.LeadFormDialog
import ru.boilercalc.app.core.ui.components.SliderWithTextField
import ru.boilercalc.app.core.ui.components.UnitToggleChip
import ru.boilercalc.app.core.util.Formatting

@Composable
fun SteamPropertiesScreen(
    viewModel: SteamPropertiesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val leadRepository = remember { LeadRepository() }
    var showLeadForm by remember { mutableStateOf(false) }
    var leadSubmitting by remember { mutableStateOf(false) }
    var leadError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Свойства насыщенного пара",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════════════════════════════
        // Section: Параметры пара
        // ═══════════════════════════════════════
        SectionHeader(text = "Параметры пара")
        Spacer(modifier = Modifier.height(8.dp))

        // Pressure slider + text
        SliderWithTextField(
            label = "Давление (избыточное)",
            value = state.pressureBar.toFloat(),
            textValue = state.pressureText,
            valueRange = 0f..16f,
            onSliderChange = { viewModel.onPressureSliderChange(it) },
            onTextChange = { viewModel.onPressureChange(it) },
            unitText = state.pressureUnit,
            onUnitClick = { viewModel.togglePressureUnit() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Temperature slider + text
        SliderWithTextField(
            label = "Температура насыщения",
            value = state.temperatureC.toFloat(),
            textValue = state.temperatureText,
            valueRange = 99f..204.3f,
            onSliderChange = { viewModel.onTemperatureSliderChange(it) },
            onTextChange = { viewModel.onTemperatureChange(it) },
            unitText = state.tempUnit,
            onUnitClick = { viewModel.toggleTempUnit() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Steam capacity slider + text
        SliderWithTextField(
            label = "Паропроизводительность",
            value = state.steamCapacityKgH.toFloat(),
            textValue = state.steamCapacityText,
            valueRange = 0f..15000f,
            onSliderChange = { viewModel.onSteamCapacitySliderChange(it) },
            onTextChange = { viewModel.onSteamCapacityChange(it) },
            unitText = state.capacityUnit,
            onUnitClick = { viewModel.toggleCapacityUnit() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ═══════════════════════════════════════
        // Section: Характеристики топлива
        // ═══════════════════════════════════════
        SectionHeader(text = "Характеристики топлива")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Calorific value
            OutlinedTextField(
                value = state.calorificText,
                onValueChange = { viewModel.onCalorificChange(it) },
                label = { Text("Q\u043D, ккал/м\u00B3") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            // Efficiency
            OutlinedTextField(
                value = state.efficiencyText,
                onValueChange = { viewModel.onEfficiencyChange(it) },
                label = { Text("КПД, %") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════════════════════════════
        // Section: Результаты
        // ═══════════════════════════════════════
        SectionHeader(text = "Результаты")
        Spacer(modifier = Modifier.height(8.dp))

        val props = state.steamProps
        if (props != null) {
            // Steam properties results card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Temperature
                    ResultRow(
                        label = "Температура насыщения",
                        value = viewModel.displayTemperature(),
                        unit = state.tempUnit,
                        onUnitClick = { viewModel.toggleTempUnit() }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Enthalpy h' (water)
                    ResultRow(
                        label = "Энтальпия воды h\u2032",
                        value = Formatting.formatNumber(props.hPrime, 1)
                            .trimEnd('0').trimEnd(','),
                        unit = "кДж/кг"
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Enthalpy h'' (steam)
                    ResultRow(
                        label = "Энтальпия пара h\u2033",
                        value = Formatting.formatNumber(props.hDoublePrime, 1)
                            .trimEnd('0').trimEnd(','),
                        unit = "кДж/кг"
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Latent heat r
                    ResultRow(
                        label = "Скрытая теплота r",
                        value = Formatting.formatNumber(props.latentHeat, 1)
                            .trimEnd('0').trimEnd(','),
                        unit = "кДж/кг"
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Specific volume
                    ResultRow(
                        label = "Удельный объём",
                        value = Formatting.formatNumber(props.specificVolume, 4)
                            .trimEnd('0').trimEnd(','),
                        unit = "м\u00B3/кг"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Power & gas consumption card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultRow(
                        label = "Тепловая мощность",
                        value = viewModel.displayPower(),
                        unit = state.powerUnit,
                        onUnitClick = { viewModel.togglePowerUnit() }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    ResultRow(
                        label = "Расход газа",
                        value = Formatting.formatNumber(state.gasConsumption, 1)
                            .trimEnd('0').trimEnd(','),
                        unit = state.gasUnit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════════════════════════════
        // Section: Подбор котлов
        // ═══════════════════════════════════════
        if (state.selectedBoilers.isNotEmpty()) {
            SectionHeader(text = "Подбор котлов")
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Рекомендуемая комбинация для D = ${
                    Formatting.formatNumber(state.steamCapacityKgH, 0)
                } кг/ч:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            state.selectedBoilers.forEach { boiler ->
                val gasPerUnit = if (boiler.loadPercent > 0) {
                    state.gasConsumption * (boiler.model.maxSteam.toDouble() * boiler.count *
                            (boiler.loadPercent / 100.0)) /
                            state.steamCapacityKgH.coerceAtLeast(1.0)
                } else {
                    0.0
                }

                BoilerCard(
                    boiler = boiler,
                    gasConsumptionPerUnit = gasPerUnit,
                    pressureBar = state.pressureBar
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ═══════════════════════════════════════
        // Section: Паровая таблица
        // ═══════════════════════════════════════
        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(text = "Паровая таблица (IAPWS-IF97)")
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Насыщенный пар · избыточное давление 0–16 бар",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Column {
                // Header
                SteamTableRow(
                    pressure = "P, бар",
                    temperature = "T, °C",
                    hPrime = "h′, кДж/кг",
                    hDoublePrime = "h″, кДж/кг",
                    latentHeat = "r, кДж/кг",
                    specificVolume = "v″, м³/кг",
                    isHeader = true
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 2.dp
                )
                SteamTable.entries.forEach { entry ->
                    SteamTableRow(
                        pressure = Formatting.formatNumber(entry.pressureGauge, 1).trimEnd('0').trimEnd(','),
                        temperature = Formatting.formatNumber(entry.temperature, 1).trimEnd('0').trimEnd(','),
                        hPrime = Formatting.formatNumber(entry.hPrime, 1).trimEnd('0').trimEnd(','),
                        hDoublePrime = Formatting.formatNumber(entry.hDoublePrime, 1).trimEnd('0').trimEnd(','),
                        latentHeat = Formatting.formatNumber(entry.latentHeat, 1).trimEnd('0').trimEnd(','),
                        specificVolume = Formatting.formatNumber(entry.specificVolume, 3).trimEnd('0').trimEnd(',')
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                }
            }
        }

        // ═══ Кнопка заявки ═══
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showLeadForm = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "Получить предложение",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Lead form dialog
    if (showLeadForm) {
        LeadFormDialog(
            context = "steam-calculator",
            onSubmit = { name, phone, region ->
                leadSubmitting = true
                leadError = null
                scope.launch {
                    val boilerName = state.selectedBoilers.firstOrNull()
                        ?.model?.name ?: ""
                    val data = LeadFormData(
                        name = name,
                        phone = phone,
                        region = region,
                        context = "steam-calculator",
                        model = boilerName,
                        boilerType = "steam",
                        pressure = state.pressureBar,
                        capacity = state.steamCapacityKgH,
                        timestamp = System.currentTimeMillis().toString()
                    )
                    leadRepository.submitLead(data)
                        .onSuccess {
                            leadSubmitting = false
                            showLeadForm = false
                        }
                        .onFailure {
                            leadSubmitting = false
                            leadError = it.message
                        }
                }
            },
            onDismiss = {
                if (!leadSubmitting) {
                    showLeadForm = false
                    leadError = null
                }
            },
            isSubmitting = leadSubmitting,
            error = leadError
        )
    }
}

// ═══════════════════════════════════════
// Helper Composables
// ═══════════════════════════════════════

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SteamTableRow(
    pressure: String,
    temperature: String,
    hPrime: String,
    hDoublePrime: String,
    latentHeat: String,
    specificVolume: String,
    isHeader: Boolean = false
) {
    val colWidth = 90.dp
    val textStyle = if (isHeader) {
        MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace)
    }
    val color = if (isHeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Row(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(pressure, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(temperature, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(hPrime, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        Text(hDoublePrime, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        Text(latentHeat, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        Text(specificVolume, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    unit: String,
    onUnitClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(4.dp))

        if (onUnitClick != null) {
            UnitToggleChip(
                unitText = unit,
                onClick = onUnitClick
            )
        } else {
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
