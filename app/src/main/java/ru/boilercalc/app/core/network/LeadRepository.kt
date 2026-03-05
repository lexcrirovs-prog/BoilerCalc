package ru.boilercalc.app.core.network

import ru.boilercalc.app.core.model.LeadFormData

class LeadRepository(
    private val api: LeadApiService = NetworkModule.leadApiService
) {

    suspend fun submitLead(data: LeadFormData): Result<Unit> {
        return try {
            val response = api.submitLead(data)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Ошибка сервера: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Ошибка сети: ${e.localizedMessage ?: "Нет соединения"}")
            )
        }
    }
}
