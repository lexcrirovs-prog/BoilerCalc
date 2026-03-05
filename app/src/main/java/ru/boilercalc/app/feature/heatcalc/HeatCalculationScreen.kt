package ru.boilercalc.app.feature.heatcalc

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.boilercalc.app.core.ui.components.SearchableDropdown
import ru.boilercalc.app.core.util.Formatting
import ru.boilercalc.app.feature.heatcalc.components.BuildingCard
import ru.boilercalc.app.feature.heatcalc.components.HeatResultsSection

@Composable
fun HeatCalculationScreen(
    viewModel: HeatCalculationViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ═══ Title ═══
        Text(
            text = "Расчёт тепловой нагрузки",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ═══ Section: Город ═══
        SectionHeader("Населённый пункт")
        Spacer(modifier = Modifier.height(8.dp))

        SearchableDropdown(
            query = state.cityQuery,
            onQueryChange = { viewModel.onCityQueryChange(it) },
            isExpanded = state.isCityDropdownExpanded,
            onDismiss = { viewModel.dismissCityDropdown() },
            filteredCities = state.filteredCities,
            onCitySelected = { viewModel.selectCity(it) }
        )

        // Climate data for selected city
        state.selectedCity?.let { city ->
            Spacer(modifier = Modifier.height(8.dp))
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ClimateRow("Расчётная t наружного воздуха", "${city.tDesign}°C")
                    ClimateRow("Средняя t отопительного периода", "${city.tHeating}°C")
                    ClimateRow("Продолжительность отоп. периода", "${city.heatingDays} сут")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══ Section: Здания ═══
        SectionHeader("Здания")
        Spacer(modifier = Modifier.height(8.dp))

        state.buildings.forEachIndexed { index, building ->
            BuildingCard(
                building = building,
                displayIndex = index + 1,
                canRemove = state.buildings.size > 1,
                onUpdate = { viewModel.updateBuilding(building.id, it) },
                onRemove = { viewModel.removeBuilding(building.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { viewModel.addBuilding() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Добавить здание")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══ Section: ГВС ═══
        SectionHeader("Горячее водоснабжение (ГВС)")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = if (state.showers > 0) state.showers.toString() else "",
                onValueChange = {
                    viewModel.onShowersChange(it.toIntOrNull() ?: 0)
                },
                label = { Text("Душевые") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text("× 8 кВт") }
            )
            OutlinedTextField(
                value = if (state.sinks > 0) state.sinks.toString() else "",
                onValueChange = {
                    viewModel.onSinksChange(it.toIntOrNull() ?: 0)
                },
                label = { Text("Умывальники") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text("× 1,2 кВт") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ═══ Кнопка Рассчитать ═══
        Button(
            onClick = { viewModel.calculate() },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.selectedCity != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Рассчитать",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // ═══ Результаты ═══
        if (state.isCalculated) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            HeatResultsSection(
                qHeating = state.qHeating,
                qDHW = state.qDHW,
                qTotal = state.qTotal,
                annualHeat = state.annualHeat,
                gsop = state.gsop,
                selectedBoilers = state.selectedBoilers
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ═══ Helper Composables ═══

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
private fun ClimateRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
