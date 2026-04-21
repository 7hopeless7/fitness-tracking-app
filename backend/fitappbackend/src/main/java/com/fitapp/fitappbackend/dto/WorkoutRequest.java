package com.fitapp.fitappbackend.dto;

import java.util.List;

public class WorkoutRequest {
    private String name;
    private List<WorkoutExerciseRequest> exercises;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkoutExerciseRequest> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExerciseRequest> exercises) {
        this.exercises = exercises;
    }
}