package com.fitapp.fitappbackend.service;

import com.fitapp.fitappbackend.dto.ExerciseLibraryResponse;
import com.fitapp.fitappbackend.dto.GenerateWorkoutRequest;
import com.fitapp.fitappbackend.dto.GeneratedWorkoutExercise;
import com.fitapp.fitappbackend.dto.GeneratedWorkoutPlanResponse;
import com.fitapp.fitappbackend.dto.WorkoutExerciseRequest;
import com.fitapp.fitappbackend.dto.WorkoutExerciseResponse;
import com.fitapp.fitappbackend.dto.WorkoutRequest;
import com.fitapp.fitappbackend.dto.WorkoutResponse;
import com.fitapp.fitappbackend.model.User;
import com.fitapp.fitappbackend.model.WorkoutPlan;
import com.fitapp.fitappbackend.model.WorkoutPlanExercise;
import com.fitapp.fitappbackend.repository.UserRepository;
import com.fitapp.fitappbackend.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final UserRepository userRepository;
    private final Map<String, List<String>> exerciseLibrary = new LinkedHashMap<>();

    public WorkoutService(WorkoutPlanRepository workoutPlanRepository, UserRepository userRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.userRepository = userRepository;

        exerciseLibrary.put("CHEST", List.of("Barbell Bench Press", "Incline Barbell Bench Press", "Decline Barbell Bench Press", "Dumbbell Bench Press", "Incline Dumbbell Press", "Chest Fly Machine", "Cable Chest Fly", "Pec Deck", "Push-ups", "Dips (Chest)"));
        exerciseLibrary.put("BACK", List.of("Pull-ups", "Chin-ups", "Lat Pulldown", "Close Grip Lat Pulldown", "Seated Cable Row", "Barbell Row", "Dumbbell Row", "T-Bar Row", "Straight Arm Pulldown", "Deadlift", "Rack Pull"));
        exerciseLibrary.put("SHOULDERS", List.of("Barbell Shoulder Press", "Dumbbell Shoulder Press", "Machine Shoulder Press", "Lateral Raises", "Cable Lateral Raises", "Front Raises", "Rear Delt Fly Machine", "Reverse Pec Deck", "Face Pull", "Upright Row"));
        exerciseLibrary.put("BICEPS", List.of("Barbell Curl", "EZ Bar Curl", "Dumbbell Curl", "Hammer Curl", "Cable Curl", "Preacher Curl", "Incline Dumbbell Curl", "Concentration Curl"));
        exerciseLibrary.put("TRICEPS", List.of("Tricep Pushdown", "Rope Pushdown", "Overhead Dumbbell Extension", "Cable Overhead Extension", "Skull Crushers", "Close Grip Bench Press", "Dips (Triceps)", "Single Arm Cable Pushdown"));
        exerciseLibrary.put("LEGS", List.of("Squat (Barbell)", "Smith Machine Squat", "Leg Press", "Hack Squat", "Bulgarian Split Squat", "Lunges", "Leg Extension", "Leg Curl (Lying)", "Leg Curl (Seated)", "Romanian Deadlift", "Calf Raises (Standing)", "Calf Raises (Seated)"));
        exerciseLibrary.put("CORE", List.of("Crunches", "Hanging Leg Raises", "Cable Crunch", "Plank", "Ab Wheel Rollout", "Russian Twists"));
    }

    public List<WorkoutResponse> getWorkouts(Long userId) {
        return workoutPlanRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(this::toWorkoutResponse)
                .toList();
    }

    public WorkoutResponse createWorkout(Long userId, WorkoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutPlan plan = new WorkoutPlan();
        plan.setUser(user);
        applyRequest(plan, request);

        return toWorkoutResponse(workoutPlanRepository.save(plan));
    }

    public WorkoutResponse updateWorkout(Long userId, Long workoutId, WorkoutRequest request) {
        WorkoutPlan existing = workoutPlanRepository.findByIdAndUserId(workoutId, userId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        applyRequest(existing, request);
        return toWorkoutResponse(workoutPlanRepository.save(existing));
    }

    public void deleteWorkout(Long userId, Long workoutId) {
        WorkoutPlan existing = workoutPlanRepository.findByIdAndUserId(workoutId, userId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        workoutPlanRepository.delete(existing);
    }

    public ExerciseLibraryResponse getExerciseLibrary() {
        return new ExerciseLibraryResponse(exerciseLibrary);
    }

    public GeneratedWorkoutPlanResponse generateWorkout(GenerateWorkoutRequest request) {
        String level = request.getLevel() == null ? "BEGINNER" : request.getLevel().toUpperCase();
        int duration = request.getDurationMinutes() == null ? 45 : request.getDurationMinutes();

        int targetExercises = duration <= 30 ? 4 : duration <= 45 ? 6 : duration <= 60 ? 8 : 10;
        if ("BEGINNER".equals(level)) {
            targetExercises = Math.max(4, targetExercises - 1);
        }

        List<String> pool = exerciseLibrary.values().stream().flatMap(List::stream).collect(Collectors.toList());
        List<GeneratedWorkoutExercise> selected = new ArrayList<>();

        for (int i = 0; i < targetExercises && i < pool.size(); i++) {
            String exerciseName = pool.get(i);
            int sets = "ADVANCED".equals(level) ? 4 : 3;
            String reps = "ADVANCED".equals(level) ? "6-10" : "INTERMEDIATE".equals(level) ? "8-12" : "10-15";
            selected.add(new GeneratedWorkoutExercise(exerciseName, sets, reps, null));
        }

        String workoutName = switch (level) {
            case "ADVANCED" -> "AI Advanced Strength Session";
            case "INTERMEDIATE" -> "AI Intermediate Hypertrophy Session";
            default -> "AI Beginner Full Body Session";
        };

        return new GeneratedWorkoutPlanResponse(workoutName, selected);
    }

    private void applyRequest(WorkoutPlan plan, WorkoutRequest request) {
        String workoutName = request.getName() == null || request.getName().isBlank() ? "Untitled Workout" : request.getName().trim();
        plan.setName(workoutName);

        plan.getExercises().clear();
        if (request.getExercises() == null) {
            return;
        }

        for (WorkoutExerciseRequest exerciseRequest : request.getExercises()) {
            WorkoutPlanExercise exercise = new WorkoutPlanExercise();
            exercise.setWorkoutPlan(plan);
            exercise.setExerciseName(exerciseRequest.getName() == null ? "" : exerciseRequest.getName());
            exercise.setReps(exerciseRequest.getReps() == null ? "" : exerciseRequest.getReps());
            exercise.setWeightKg(exerciseRequest.getWeightKg() == null ? "" : exerciseRequest.getWeightKg());
            plan.getExercises().add(exercise);
        }
    }

    private WorkoutResponse toWorkoutResponse(WorkoutPlan plan) {
        List<WorkoutExerciseResponse> exercises = plan.getExercises().stream()
                .map(item -> new WorkoutExerciseResponse(item.getId(), item.getExerciseName(), item.getReps(), item.getWeightKg()))
                .toList();
        return new WorkoutResponse(plan.getId(), plan.getName(), exercises);
    }
}