package com.fitapp.fitappbackend.dto;

import java.util.List;

public class GeneratedWorkoutPlanResponse {
    private String workoutName;
    private List<GeneratedWorkoutExercise> exercises;

    public GeneratedWorkoutPlanResponse() {
    }

    public GeneratedWorkoutPlanResponse(String workoutName, List<GeneratedWorkoutExercise> exercises) {
        this.workoutName = workoutName;
        this.exercises = exercises;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public List<GeneratedWorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<GeneratedWorkoutExercise> exercises) {
        this.exercises = exercises;
    }
}