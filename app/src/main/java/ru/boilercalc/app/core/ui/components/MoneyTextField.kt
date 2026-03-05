package ru.boilercalc.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import ru.boilercalc.app.core.util.Formatting
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun MoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffix: String = "руб"
) {
    var isFocused by remember { mutableStateOf(false) }
    var displayText by remember(value) { mutableStateOf(value) }

    OutlinedTextField(
        value = if (isFocused) displayText else formatWithSpaces(displayText),
        onValueChange = { newText ->
            // Allow only digits, spaces, commas, and dots while editing
            val cleaned = newText.filter { it.isDigit() || it == ' ' || it == ',' || it == '.' }
            displayText = cleaned
            onValueChange(cleaned)
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    isFocused = true
                    // Strip formatting for editing
                    displayText = stripFormatting(displayText)
                } else {
                    isFocused = false
                    // Format for display
                    displayText = formatWithSpaces(displayText)
                }
            },
        label = { Text(label) },
        suffix = { Text(suffix) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

private val russianSymbols = DecimalFormatSymbols(Locale("ru", "RU")).apply {
    groupingSeparator = ' '
    decimalSeparator = ','
}

private fun formatWithSpaces(text: String): String {
    val cleaned = text.replace(Regex("[^0-9,.]"), "").replace(',', '.')
    val number = cleaned.toDoubleOrNull() ?: return text
    val fmt = DecimalFormat("#,##0", russianSymbols)
    return fmt.format(number)
}

private fun stripFormatting(text: String): String {
    return text.replace(Regex("[^0-9,.]"), "")
}
