package com.fitapp.android.api

import com.fitapp.android.model.AuthResponse
import com.fitapp.android.model.LoginRequest
import com.fitapp.android.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST
import com.fitapp.android.model.FoodRequest
import com.fitapp.android.model.FoodResponse

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("nutrition/analyze")
    suspend fun analyzeFood(@Body request: FoodRequest): FoodResponse
}