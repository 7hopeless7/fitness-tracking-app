package com.fitapp.fitappbackend.dto;

public class FoodRequest {
    private String foodName;
    private double grams;

    public FoodRequest() {
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getGrams() {
        return grams;
    }

    public void setGrams(double grams) {
        this.grams = grams;
    }
}