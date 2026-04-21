package com.fitapp.fitappbackend.dto;

public class GenerateWorkoutRequest {
    private String level;
    private Integer durationMinutes;
    private Integer trainingDaysPerWeek;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTrainingDaysPerWeek() {
        return trainingDaysPerWeek;
    }

    public void setTrainingDaysPerWeek(Integer trainingDaysPerWeek) {
        this.trainingDaysPerWeek = trainingDaysPerWeek;
    }
}