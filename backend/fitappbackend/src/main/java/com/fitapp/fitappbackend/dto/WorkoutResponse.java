package com.fitapp.fitappbackend.dto;

import java.util.List;

public class WorkoutResponse {
    private Long id;
    private String name;
    private List<WorkoutExerciseResponse> exercises;

    public WorkoutResponse() {
    }

    public WorkoutResponse(Long id, String name, List<WorkoutExerciseResponse> exercises) {
        this.id = id;
        this.name = name;
        this.exercises = exercises;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkoutExerciseResponse> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExerciseResponse> exercises) {
        this.exercises = exercises;
    }
}