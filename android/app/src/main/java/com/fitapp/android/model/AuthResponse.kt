package com.fitapp.android.model

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val userId: Long?,
    val username: String?
)