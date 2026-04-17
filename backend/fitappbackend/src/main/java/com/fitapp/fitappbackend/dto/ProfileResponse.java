package com.fitapp.fitappbackend.dto;

public class ProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private Integer age;
    private Double weightKg;
    private String activityLevel;
    private String goal;
    private Double recommendedCalories;

    public ProfileResponse(Long userId, String username, String email, Integer age, Double weightKg, String activityLevel, String goal, Double recommendedCalories) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.age = age;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.recommendedCalories = recommendedCalories;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public String getGoal() {
        return goal;
    }

    public Double getRecommendedCalories() {
        return recommendedCalories;
    }
}