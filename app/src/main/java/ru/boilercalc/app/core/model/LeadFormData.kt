package ru.boilercalc.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LeadFormData(
    val name: String,
    val phone: String,
    val region: String,
    val context: String,
    val model: String = "",
    val boilerType: String = "",
    val pressure: Double? = null,
    val capacity: Double? = null,
    val utm: Map<String, String> = emptyMap(),
    val timestamp: String = ""
)
