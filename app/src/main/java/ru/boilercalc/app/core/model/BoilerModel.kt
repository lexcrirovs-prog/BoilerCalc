package ru.boilercalc.app.core.model

data class BoilerModel(
    val id: String,
    val name: String,
    val minSteam: Int,
    val maxSteam: Int,
    val gas8484: Int
)
