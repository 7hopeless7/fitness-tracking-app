package com.fitapp.android.model

data class ProfileResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val age: Int?,
    val weightKg: Double?,
    val activityLevel: String?,
    val goal: String?,
    val recommendedCalories: Double?
)