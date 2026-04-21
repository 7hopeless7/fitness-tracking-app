package com.fitapp.fitappbackend.controller;

import com.fitapp.fitappbackend.dto.ExerciseLibraryResponse;
import com.fitapp.fitappbackend.dto.GenerateWorkoutRequest;
import com.fitapp.fitappbackend.dto.GeneratedWorkoutPlanResponse;
import com.fitapp.fitappbackend.dto.WorkoutRequest;
import com.fitapp.fitappbackend.dto.WorkoutResponse;
import com.fitapp.fitappbackend.service.WorkoutService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    public List<WorkoutResponse> getWorkouts(@RequestParam Long userId) {
        return workoutService.getWorkouts(userId);
    }

    @PostMapping
    public WorkoutResponse createWorkout(@RequestParam Long userId, @RequestBody WorkoutRequest request) {
        return workoutService.createWorkout(userId, request);
    }

    @PutMapping("/{workoutId}")
    public WorkoutResponse updateWorkout(
            @PathVariable Long workoutId,
            @RequestParam Long userId,
            @RequestBody WorkoutRequest request
    ) {
        return workoutService.updateWorkout(userId, workoutId, request);
    }

    @DeleteMapping("/{workoutId}")
    public void deleteWorkout(@PathVariable Long workoutId, @RequestParam Long userId) {
        workoutService.deleteWorkout(userId, workoutId);
    }

    @GetMapping("/exercises")
    public ExerciseLibraryResponse getExercises() {
        return workoutService.getExerciseLibrary();
    }

    @PostMapping("/generate")
    public GeneratedWorkoutPlanResponse generateWorkout(@RequestBody GenerateWorkoutRequest request) {
        return workoutService.generateWorkout(request);
    }
}