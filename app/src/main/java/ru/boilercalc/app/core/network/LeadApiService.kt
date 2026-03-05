package ru.boilercalc.app.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.boilercalc.app.core.model.LeadFormData

interface LeadApiService {

    @POST("api/leads")
    suspend fun submitLead(@Body data: LeadFormData): Response<Unit>
}
