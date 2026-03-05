package ru.boilercalc.app.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Formatting {
    private val russianSymbols = DecimalFormatSymbols(Locale("ru", "RU")).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }

    fun formatMoney(value: Double): String {
        val fmt = DecimalFormat("#,##0", russianSymbols)
        return "${fmt.format(value)} руб"
    }

    fun formatNumber(value: Double, decimals: Int = 2): String {
        val pattern = if (decimals > 0) "#,##0.${"0".repeat(decimals)}" else "#,##0"
        val fmt = DecimalFormat(pattern, russianSymbols)
        return fmt.format(value)
    }

    fun parseMoney(text: String): Double {
        val cleaned = text.replace(Regex("[^0-9,.]"), "").replace(',', '.')
        return cleaned.toDoubleOrNull() ?: 0.0
    }
}
