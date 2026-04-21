package com.fitapp.fitappbackend.dto;

public class WorkoutExerciseResponse {
    private Long id;
    private String name;
    private String reps;
    private String weightKg;

    public WorkoutExerciseResponse() {
    }

    public WorkoutExerciseResponse(Long id, String name, String reps, String weightKg) {
        this.id = id;
        this.name = name;
        this.reps = reps;
        this.weightKg = weightKg;
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

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(String weightKg) {
        this.weightKg = weightKg;
    }
}