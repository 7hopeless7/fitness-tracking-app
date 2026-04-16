package com.fitapp.fitappbackend.dto;

public class FoodResponse {
    private String foodName;
    private double grams;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;

    public FoodResponse() {
    }

    public FoodResponse(String foodName, double grams, double calories, double protein, double carbs, double fat) {
        this.foodName = foodName;
        this.grams = grams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
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