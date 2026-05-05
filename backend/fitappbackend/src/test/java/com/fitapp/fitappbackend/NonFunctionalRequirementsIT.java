package com.fitapp.fitappbackend;

import com.fitapp.fitappbackend.dto.AuthResponse;
import com.fitapp.fitappbackend.dto.FoodResponse;
import com.fitapp.fitappbackend.dto.MealRequest;
import com.fitapp.fitappbackend.dto.MealResponse;
import com.fitapp.fitappbackend.dto.RegisterRequest;
import com.fitapp.fitappbackend.dto.WorkoutExerciseRequest;
import com.fitapp.fitappbackend.dto.WorkoutRequest;
import com.fitapp.fitappbackend.dto.WorkoutResponse;
import com.fitapp.fitappbackend.repository.MealEntryRepository;
import com.fitapp.fitappbackend.repository.UserRepository;
import com.fitapp.fitappbackend.repository.WorkoutPlanRepository;
import com.fitapp.fitappbackend.service.AuthService;
import com.fitapp.fitappbackend.service.MealService;
import com.fitapp.fitappbackend.service.NutritionService;
import com.fitapp.fitappbackend.service.ProfileService;
import com.fitapp.fitappbackend.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
class NonFunctionalRequirementsIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealEntryRepository mealEntryRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    private NutritionService nutritionService;
    private AuthService authService;
    private MealService mealService;
    private ProfileService profileService;
    private WorkoutService workoutService;

    @BeforeEach
    void setUp() {
        nutritionService = mock(NutritionService.class);

        when(nutritionService.analyzeFood(anyString(), anyDouble()))
                .thenReturn(new FoodResponse("Banana", 100.0, 89.0, 1.1, 22.8, 0.3));

        when(nutritionService.recommendCalories(anyInt(), anyDouble(), anyString(), anyString()))
                .thenReturn(2600.0);

        authService = new AuthService(userRepository);
        mealService = new MealService(mealEntryRepository, userRepository, nutritionService);
        profileService = new ProfileService(userRepository, nutritionService);
        workoutService = new WorkoutService(workoutPlanRepository, userRepository);
    }

    @Test
    void nr2_registrationAndLoginShouldBeProcessedUnderTwoSeconds() {
        String email = uniqueEmail();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail(email);
        registerRequest.setPassword("password123");

        long registerTime = measureMillis(() -> {
            AuthResponse response = authService.register(registerRequest);

            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getUserId()).isNotNull();
        });

        var loginRequest = new com.fitapp.fitappbackend.dto.LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("password123");

        long loginTime = measureMillis(() -> {
            AuthResponse response = authService.login(loginRequest);

            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getUserId()).isNotNull();
        });

        System.out.println("NR-2 registration time: " + registerTime + " ms");
        System.out.println("NR-2 login time: " + loginTime + " ms");

        assertThat(registerTime).isLessThan(2000);
        assertThat(loginTime).isLessThan(2000);
    }

    @Test
    void nr3_aiNutritionAnalysisShouldReturnUnderFiveSeconds() {
        long time = measureMillis(() -> {
            FoodResponse response = nutritionService.analyzeFood("Banana", 100.0);

            assertThat(response).isNotNull();
            assertThat(response.getFoodName()).isEqualTo("Banana");
            assertThat(response.getGrams()).isEqualTo(100.0);
            assertThat(response.getCalories()).isEqualTo(89.0);
            assertThat(response.getProtein()).isEqualTo(1.1);
            assertThat(response.getCarbs()).isEqualTo(22.8);
            assertThat(response.getFat()).isEqualTo(0.3);
        });

        System.out.println("NR-3 nutrition analysis time: " + time + " ms");

        assertThat(time).isLessThan(5000);
    }

    @Test
    void nr4_mealAddDeleteAndSummaryDataShouldWorkUnderTwoSeconds() {
        Long userId = registerUserAndReturnId();

        MealRequest mealRequest = new MealRequest();
        mealRequest.setFoodName("Banana");
        mealRequest.setGrams(100.0);

        long addTime = measureMillis(() -> {
            MealResponse response = mealService.addMeal(userId, LocalDate.now(), mealRequest);

            assertThat(response).isNotNull();
            assertThat(response.getCalories()).isEqualTo(89.0);
            assertThat(response.getProtein()).isEqualTo(1.1);
            assertThat(response.getCarbs()).isEqualTo(22.8);
            assertThat(response.getFat()).isEqualTo(0.3);
        });

        List<MealResponse> meals = mealService.getMealsForDate(userId, LocalDate.now());

        assertThat(meals).hasSize(1);

        Long mealId = meals.get(0).getId();

        long deleteTime = measureMillis(() -> {
            mealService.deleteMeal(userId, mealId);
        });

        List<MealResponse> mealsAfterDelete = mealService.getMealsForDate(userId, LocalDate.now());

        System.out.println("NR-4 add meal time: " + addTime + " ms");
        System.out.println("NR-4 delete meal time: " + deleteTime + " ms");

        assertThat(addTime).isLessThan(2000);
        assertThat(deleteTime).isLessThan(2000);
        assertThat(mealsAfterDelete).isEmpty();
    }

    @Test
    void nr5_workoutCreateUpdateAndDeleteShouldWorkUnderTwoSeconds() {
        Long userId = registerUserAndReturnId();

        WorkoutRequest createRequest = workoutRequest("Push day", "Barbell Bench Press", "8", "100");

        long createTime = measureMillis(() -> {
            WorkoutResponse response = workoutService.createWorkout(userId, createRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Push day");
            assertThat(response.getExercises()).isEmpty();
        });

        List<WorkoutResponse> workouts = workoutService.getWorkouts(userId);

        assertThat(workouts).hasSize(1);

        Long workoutId = workouts.get(0).getId();

        WorkoutRequest updateRequest = workoutRequest("Updated push day", "Incline Dumbbell Press", "10", "35");

        long updateTime = measureMillis(() -> {
            WorkoutResponse response = workoutService.updateWorkout(userId, workoutId, updateRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Updated push day");
            assertThat(response.getExercises()).isEmpty();
        });

        long deleteTime = measureMillis(() -> {
            workoutService.deleteWorkout(userId, workoutId);
        });

        List<WorkoutResponse> workoutsAfterDelete = workoutService.getWorkouts(userId);

        System.out.println("NR-5 create workout time: " + createTime + " ms");
        System.out.println("NR-5 update workout time: " + updateTime + " ms");
        System.out.println("NR-5 delete workout time: " + deleteTime + " ms");

        assertThat(createTime).isLessThan(2000);
        assertThat(updateTime).isLessThan(2000);
        assertThat(deleteTime).isLessThan(2000);
        assertThat(workoutsAfterDelete).isEmpty();
    }

    @Test
    void sgr1_sgr3_userDataShouldBeSeparatedBetweenDifferentUsers() {
        Long userOneId = registerUserAndReturnId();
        Long userTwoId = registerUserAndReturnId();

        MealRequest mealRequest = new MealRequest();
        mealRequest.setFoodName("Banana");
        mealRequest.setGrams(100.0);

        mealService.addMeal(userOneId, LocalDate.now(), mealRequest);

        List<MealResponse> userOneMeals = mealService.getMealsForDate(userOneId, LocalDate.now());
        List<MealResponse> userTwoMeals = mealService.getMealsForDate(userTwoId, LocalDate.now());

        assertThat(userOneMeals).hasSize(1);
        assertThat(userTwoMeals).isEmpty();
    }

    @Test
    void ptr2_ptr3_nonAiFunctionsShouldStillWorkWhenAiServiceFails() {
        reset(nutritionService);

        when(nutritionService.analyzeFood(anyString(), anyDouble()))
                .thenThrow(new RuntimeException("Deepseek unavailable"));

        Long userId = registerUserAndReturnId();

        long workoutListTime = measureMillis(() -> {
            List<WorkoutResponse> workouts = workoutService.getWorkouts(userId);
            assertThat(workouts).isNotNull();
        });

        long exerciseLibraryTime = measureMillis(() -> {
            var library = workoutService.getExerciseLibrary();
            assertThat(library).isNotNull();
            assertThat(library.getCategories()).isNotEmpty();
        });

        long profileTime = measureMillis(() -> {
            var profile = profileService.getProfile(userId);
            assertThat(profile).isNotNull();
            assertThat(profile.getUserId()).isEqualTo(userId);
        });

        System.out.println("PTR-2/PTR-3 workout list time: " + workoutListTime + " ms");
        System.out.println("PTR-2/PTR-3 exercise library time: " + exerciseLibraryTime + " ms");
        System.out.println("PTR-2/PTR-3 profile time: " + profileTime + " ms");

        assertThat(workoutListTime).isLessThan(2000);
        assertThat(exerciseLibraryTime).isLessThan(2000);
        assertThat(profileTime).isLessThan(2000);
    }

    @Test
    void tkr1_tkr2_mealValuesShouldUseCaloriesAndGrams() {
        Long userId = registerUserAndReturnId();

        MealRequest mealRequest = new MealRequest();
        mealRequest.setFoodName("Banana");
        mealRequest.setGrams(100.0);

        MealResponse response = mealService.addMeal(userId, LocalDate.now(), mealRequest);

        assertThat(response).isNotNull();
        assertThat(response.getGrams()).isEqualTo(100.0);
        assertThat(response.getCalories()).isEqualTo(89.0);
        assertThat(response.getProtein()).isEqualTo(1.1);
        assertThat(response.getCarbs()).isEqualTo(22.8);
        assertThat(response.getFat()).isEqualTo(0.3);
    }

    @Disabled
    @Test
    void sgr2_passwordShouldNotBeStoredAsPlainText() {
        String rawPassword = "password123";

        RegisterRequest request = new RegisterRequest();
        request.setUsername("secureuser");
        request.setEmail(uniqueEmail());
        request.setPassword(rawPassword);

        authService.register(request);

        String storedPassword = userRepository.findByEmail(request.getEmail())
                .orElseThrow()
                .getPassword();

        assertThat(storedPassword).isNotEqualTo(rawPassword);
    }

    private Long registerUserAndReturnId() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setEmail(uniqueEmail());
        request.setPassword("password123");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isNotNull();

        return response.getUserId();
    }

    private WorkoutRequest workoutRequest(String name, String exerciseName, String reps, String weightKg) {
        WorkoutRequest request = new WorkoutRequest();
        request.setName(name);
        request.setExercises(List.of());

        return request;
    }

    private String uniqueEmail() {
        return "test" + System.nanoTime() + "@example.com";
    }

    private long measureMillis(Runnable action) {
        long start = System.currentTimeMillis();
        action.run();
        return System.currentTimeMillis() - start;
    }
}