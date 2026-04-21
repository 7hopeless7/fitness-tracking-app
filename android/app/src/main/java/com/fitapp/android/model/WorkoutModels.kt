package com.fitapp.android.model

data class ExerciseLibraryResponse(
    val categories: Map<String, List<String>>
)

data class GenerateWorkoutRequest(
    val level: String,
    val durationMinutes: Int,
    val trainingDaysPerWeek: Int
)

data class GeneratedWorkoutExercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val targetWeightKg: Double?
)

data class GeneratedWorkoutPlanResponse(
    val workoutName: String,
    val exercises: List<GeneratedWorkoutExercise>
)

data class WorkoutExercisePayload(
    val name: String,
    val reps: String,
    val weightKg: String
)

data class WorkoutPayload(
    val name: String,
    val exercises: List<WorkoutExercisePayload>
)

data class WorkoutExerciseResponse(
    val id: Long,
    val name: String,
    val reps: String,
    val weightKg: String
)

data class WorkoutResponse(
    val id: Long,
    val name: String,
    val exercises: List<WorkoutExerciseResponse>
)