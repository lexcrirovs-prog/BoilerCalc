package ru.boilercalc.app.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Reusable component combining a labeled Slider with an OutlinedTextField
 * and an optional UnitToggleChip. Slider and TextField are bidirectionally
 * synchronized through the parent (via callbacks).
 *
 * @param label Text label displayed above the row
 * @param value Current numeric value (for the slider position)
 * @param textValue Current text representation of the value (for the text field)
 * @param valueRange Range for the slider
 * @param onSliderChange Callback when slider value changes
 * @param onTextChange Callback when text field value changes
 * @param unitText Optional unit text for the toggle chip
 * @param onUnitClick Optional callback for clicking the unit chip
 * @param modifier Optional modifier
 * @param steps Number of discrete steps (0 for continuous)
 */
@Composable
fun SliderWithTextField(
    label: String,
    value: Float,
    textValue: String,
    valueRange: ClosedFloatingPointRange<Float>,
    onSliderChange: (Float) -> Unit,
    onTextChange: (String) -> Unit,
    unitText: String? = null,
    onUnitClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    steps: Int = 0
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = value.coerceIn(valueRange),
                onValueChange = onSliderChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = textValue,
                onValueChange = onTextChange,
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            if (unitText != null && onUnitClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                UnitToggleChip(
                    unitText = unitText,
                    onClick = onUnitClick
                )
            }
        }
    }
}
