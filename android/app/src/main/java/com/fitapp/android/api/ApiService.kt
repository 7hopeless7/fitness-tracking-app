package com.fitapp.android.api

import com.fitapp.android.model.AuthResponse
import com.fitapp.android.model.ExerciseLibraryResponse
import com.fitapp.android.model.GenerateWorkoutRequest
import com.fitapp.android.model.GeneratedWorkoutPlanResponse
import com.fitapp.android.model.LoginRequest
import com.fitapp.android.model.MealItem
import com.fitapp.android.model.MealRequest
import com.fitapp.android.model.ProfileRequest
import com.fitapp.android.model.ProfileResponse
import com.fitapp.android.model.RegisterRequest
import com.fitapp.android.model.WorkoutPayload
import com.fitapp.android.model.WorkoutResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("meals")
    suspend fun getMeals(
        @Query("userId") userId: Long,
        @Query("date") date: String
    ): List<MealItem>

    @POST("meals")
    suspend fun addMeal(
        @Query("userId") userId: Long,
        @Query("date") date: String,
        @Body request: MealRequest
    ): MealItem

    @PUT("meals/{mealId}")
    suspend fun updateMeal(
        @Path("mealId") mealId: Long,
        @Query("userId") userId: Long,
        @Body request: MealRequest
    ): MealItem

    @DELETE("meals/{mealId}")
    suspend fun deleteMeal(
        @Path("mealId") mealId: Long,
        @Query("userId") userId: Long
    )

    @GET("workouts")
    suspend fun getWorkouts(
        @Query("userId") userId: Long
    ): List<WorkoutResponse>

    @POST("workouts")
    suspend fun createWorkout(
        @Query("userId") userId: Long,
        @Body request: WorkoutPayload
    ): WorkoutResponse

    @PUT("workouts/{workoutId}")
    suspend fun updateWorkout(
        @Path("workoutId") workoutId: Long,
        @Query("userId") userId: Long,
        @Body request: WorkoutPayload
    ): WorkoutResponse

    @DELETE("workouts/{workoutId}")
    suspend fun deleteWorkout(
        @Path("workoutId") workoutId: Long,
        @Query("userId") userId: Long
    )

    @GET("workouts/exercises")
    suspend fun getExerciseLibrary(): ExerciseLibraryResponse

    @POST("workouts/generate")
    suspend fun generateWorkoutPlan(
        @Body request: GenerateWorkoutRequest
    ): GeneratedWorkoutPlanResponse

    @GET("profile")
    suspend fun getProfile(
        @Query("userId") userId: Long
    ): ProfileResponse

    @PUT("profile")
    suspend fun updateProfile(
        @Query("userId") userId: Long,
        @Body request: ProfileRequest
    ): ProfileResponse
}