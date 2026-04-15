package com.fitapp.android.api

import com.fitapp.android.model.AuthResponse
import com.fitapp.android.model.LoginRequest
import com.fitapp.android.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}