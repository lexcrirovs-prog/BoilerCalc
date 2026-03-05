package ru.boilercalc.app.feature.economics

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.boilercalc.app.core.model.EconBoilerModel
import ru.boilercalc.app.core.model.LeadFormData
import ru.boilercalc.app.core.network.LeadRepository
import ru.boilercalc.app.core.ui.components.LeadFormDialog
import ru.boilercalc.app.core.ui.components.MoneyTextField
import ru.boilercalc.app.core.util.Formatting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EconomicsScreen(
    viewModel: EconomicsViewModel = viewModel()
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
        // ═══ Title ═══
        Text(
            text = "Экономика котельной",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ═══ Section 1: Категория и модель котла ═══
        SectionHeader("Выбор котла")
        Spacer(modifier = Modifier.height(8.dp))

        // Category selector
        val categories = listOf("steam" to "Паровой", "water" to "Водогр. C", "waterE" to "Водогр. E")
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            categories.forEachIndexed { index, (key, label) ->
                SegmentedButton(
                    selected = state.boilerCategory == key,
                    onClick = { viewModel.selectCategory(key) },
                    shape = SegmentedButtonDefaults.itemShape(index, categories.size)
                ) {
                    Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Model dropdown
        val models = viewModel.getModelsForCategory(state.boilerCategory)
        ModelDropdown(
            models = models,
            selected = state.selectedModel,
            onSelect = { viewModel.selectModel(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Boiler count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.boilerCount.toString(),
                onValueChange = { viewModel.onBoilerCountChange(it.toIntOrNull() ?: 1) },
                label = { Text("Кол-во котлов") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Economizer checkbox
            if (state.selectedModel?.economizerPrice != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = state.useEconomizer,
                        onCheckedChange = { viewModel.onUseEconomizerChange(it) }
                    )
                    Text(
                        text = "Экономайзер",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // CAPEX card
        if (state.selectedModel != null && state.capex > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "CAPEX (капитальные затраты)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatting.formatMoney(state.capex),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══ Section 2: OPEX ═══
        SectionHeader("OPEX (операционные расходы)")
        Spacer(modifier = Modifier.height(8.dp))

        // OPEX inputs — row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.loadText,
                onValueChange = { viewModel.onLoadChange(it) },
                label = { Text("Нагрузка, %") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            OutlinedTextField(
                value = state.dailyHoursText,
                onValueChange = { viewModel.onDailyHoursChange(it) },
                label = { Text("Часов/сут") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // OPEX inputs — row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.workDaysText,
                onValueChange = { viewModel.onWorkDaysChange(it) },
                label = { Text("Суток/год") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = state.gasPriceText,
                onValueChange = { viewModel.onGasPriceChange(it) },
                label = { Text("Цена газа, руб/м³") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Maintenance
        MoneyTextField(
            value = state.maintenanceCostText,
            onValueChange = { viewModel.onMaintenanceChange(it) },
            label = "Обслуживание в год"
        )

        // OPEX result display
        if (state.selectedModel != null && state.annualOPEX > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Годовой расход газа:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${Formatting.formatNumber(state.annualGas, 0)} м³",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Выработано Гкал за год:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${Formatting.formatNumber(state.annualHeatGcal, 1)} Гкал",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Годовой OPEX:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            Formatting.formatMoney(state.annualOPEX),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══ Section 3: Доходы ═══
        SectionHeader("Доходы")
        Spacer(modifier = Modifier.height(8.dp))

        // Tariff input
        OutlinedTextField(
            value = state.tarifGcalText,
            onValueChange = { viewModel.onTarifGcalChange(it) },
            label = { Text("Тарифная стоимость 1 Гкал, руб") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Revenue mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.revenueMode == "uniform",
                onClick = { viewModel.onRevenueModeChange("uniform") },
                label = { Text("Равномерный") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            FilterChip(
                selected = state.revenueMode == "variable",
                onClick = { viewModel.onRevenueModeChange("variable") },
                label = { Text("Переменный") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.revenueMode == "uniform") {
            MoneyTextField(
                value = state.uniformRevenueText,
                onValueChange = { viewModel.onUniformRevenueChange(it) },
                label = "Валовый доход"
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Валовый доход — это общая сумма выручки, которую вы получаете от продажи произведённого тепла до вычета расходов на газ и эксплуатацию.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Years count
            OutlinedTextField(
                value = state.yearsCount.toString(),
                onValueChange = { viewModel.onYearsCountChange(it.toIntOrNull() ?: 10) },
                label = { Text("Количество лет") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Variable revenue inputs
            state.variableRevenues.take(state.yearsCount).forEachIndexed { index, revenue ->
                MoneyTextField(
                    value = if (revenue > 0) Formatting.formatNumber(revenue, 0) else "",
                    onValueChange = { viewModel.onVariableRevenueChange(index, it) },
                    label = "Год ${index + 1}"
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Discount rate
        OutlinedTextField(
            value = state.discountRateText,
            onValueChange = { viewModel.onDiscountRateChange(it) },
            label = { Text("Ставка дисконтирования, %") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ═══ Кнопка Рассчитать ═══
        Button(
            onClick = { viewModel.calculate() },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.selectedModel != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Рассчитать окупаемость",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // ═══ Результаты ═══
        if (state.isCalculated && state.paybackResult != null) {
            val result = state.paybackResult!!

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader("Результаты")
            Spacer(modifier = Modifier.height(8.dp))

            // PP / DPBP cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaybackCard(
                    title = "PP",
                    subtitle = "Простой срок",
                    value = formatPayback(result.pp),
                    modifier = Modifier.weight(1f)
                )
                PaybackCard(
                    title = "DPBP",
                    subtitle = "Дисконтированный",
                    value = formatPayback(result.dpbp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results table
            Text(
                text = "Таблица денежных потоков",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal scrollable table
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column {
                    // Header
                    TableRow(
                        year = "Год",
                        revenue = "Доход",
                        cf = "CF",
                        cumCF = "ΣCF",
                        pv = "PV",
                        cumPV = "ΣPV",
                        isHeader = true
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 2.dp
                    )

                    // Data rows
                    result.table.forEach { row ->
                        TableRow(
                            year = row.year.toString(),
                            revenue = Formatting.formatNumber(row.revenue, 0),
                            cf = Formatting.formatNumber(row.cashFlow, 0),
                            cumCF = Formatting.formatNumber(row.cumCashFlow, 0),
                            pv = Formatting.formatNumber(row.presentValue, 0),
                            cumPV = Formatting.formatNumber(row.cumPresentValue, 0),
                            isHighlighted = row.cumCashFlow >= 0 && (row.year == 1 || result.table[row.year - 2].cumCashFlow < 0)
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )
                    }
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
                text = "Получить скидку",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Lead form dialog
    if (showLeadForm) {
        val categoryLabel = when (state.boilerCategory) {
            "steam" -> "Паровой"
            "water" -> "Водогрейный C"
            "waterE" -> "Водогрейный E"
            else -> ""
        }
        LeadFormDialog(
            title = "Получить скидку",
            context = "economics-discount",
            onSubmit = { name, phone, region ->
                leadSubmitting = true
                leadError = null
                scope.launch {
                    val data = LeadFormData(
                        name = name,
                        phone = phone,
                        region = region,
                        context = "economics-discount",
                        model = state.selectedModel?.name ?: "",
                        boilerType = categoryLabel,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelDropdown(
    models: List<EconBoilerModel>,
    selected: EconBoilerModel?,
    onSelect: (EconBoilerModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.name ?: "Выберите модель",
            onValueChange = {},
            readOnly = true,
            label = { Text("Модель котла") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(model.name)
                            Text(
                                text = Formatting.formatMoney(model.price.toDouble()),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onSelect(model)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun PaybackCard(
    title: String,
    subtitle: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TableRow(
    year: String,
    revenue: String,
    cf: String,
    cumCF: String,
    pv: String,
    cumPV: String,
    isHeader: Boolean = false,
    isHighlighted: Boolean = false
) {
    val colWidth = 110.dp
    val yearWidth = 50.dp
    val textStyle = if (isHeader) {
        MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace)
    }
    val color = when {
        isHeader -> MaterialTheme.colorScheme.primary
        isHighlighted -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(text = year, style = textStyle, color = color,
            modifier = Modifier.width(yearWidth), textAlign = TextAlign.Center)
        Text(text = revenue, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = TextAlign.End)
        Text(text = cf, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = TextAlign.End)
        Text(text = cumCF, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = TextAlign.End)
        Text(text = pv, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = TextAlign.End)
        Text(text = cumPV, style = textStyle, color = color,
            modifier = Modifier.width(colWidth), textAlign = TextAlign.End)
    }
}

private fun formatPayback(years: Double): String {
    return if (years < 0) {
        "Не окупается"
    } else {
        val y = years.toInt()
        val m = ((years - y) * 12).toInt()
        if (m > 0) "$y лет $m мес." else "$y лет"
    }
}
