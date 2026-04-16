package com.fitapp.android.model

data class MealItem(
    val id: Long,
    val mealDate: String,
    val foodName: String,
    val grams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)