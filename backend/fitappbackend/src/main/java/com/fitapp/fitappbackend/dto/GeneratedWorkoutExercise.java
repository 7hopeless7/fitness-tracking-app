package com.fitapp.fitappbackend.dto;

public class GeneratedWorkoutExercise {
    private String name;
    private Integer sets;
    private String reps;
    private Double targetWeightKg;

    public GeneratedWorkoutExercise() {
    }

    public GeneratedWorkoutExercise(String name, Integer sets, String reps, Double targetWeightKg) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.targetWeightKg = targetWeightKg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public Double getTargetWeightKg() {
        return targetWeightKg;
    }

    public void setTargetWeightKg(Double targetWeightKg) {
        this.targetWeightKg = targetWeightKg;
    }
}