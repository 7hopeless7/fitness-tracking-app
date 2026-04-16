package com.fitapp.fitappbackend.dto;

import java.time.LocalDate;

public class MealResponse {
    private Long id;
    private LocalDate mealDate;
    private String foodName;
    private double grams;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;

    public MealResponse() {
    }

    public MealResponse(Long id, LocalDate mealDate, String foodName, double grams, double calories, double protein, double carbs, double fat) {
        this.id = id;
        this.mealDate = mealDate;
        this.foodName = foodName;
        this.grams = grams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getGrams() {
        return grams;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }
}