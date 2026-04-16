package com.fitapp.fitappbackend.service;

import com.fitapp.fitappbackend.dto.FoodResponse;
import com.fitapp.fitappbackend.dto.MealRequest;
import com.fitapp.fitappbackend.dto.MealResponse;
import com.fitapp.fitappbackend.model.MealEntry;
import com.fitapp.fitappbackend.model.User;
import com.fitapp.fitappbackend.repository.MealEntryRepository;
import com.fitapp.fitappbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MealService {

    private final MealEntryRepository mealEntryRepository;
    private final UserRepository userRepository;
    private final NutritionService nutritionService;

    public MealService(MealEntryRepository mealEntryRepository, UserRepository userRepository, NutritionService nutritionService) {
        this.mealEntryRepository = mealEntryRepository;
        this.userRepository = userRepository;
        this.nutritionService = nutritionService;
    }

    public List<MealResponse> getMealsForDate(Long userId, LocalDate date) {
        return mealEntryRepository.findByUserIdAndMealDateOrderByIdDesc(userId, date)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MealResponse addMeal(Long userId, LocalDate date, MealRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FoodResponse nutrition = nutritionService.analyzeFood(request.getFoodName(), request.getGrams());

        MealEntry meal = new MealEntry();
        meal.setUser(user);
        meal.setMealDate(date);
        meal.setFoodName(nutrition.getFoodName());
        meal.setGrams(nutrition.getGrams());
        meal.setCalories(nutrition.getCalories());
        meal.setProtein(nutrition.getProtein());
        meal.setCarbs(nutrition.getCarbs());
        meal.setFat(nutrition.getFat());

        return toResponse(mealEntryRepository.save(meal));
    }

    public MealResponse updateMeal(Long userId, Long mealId, MealRequest request) {
        MealEntry existing = mealEntryRepository.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        FoodResponse nutrition = nutritionService.analyzeFood(request.getFoodName(), request.getGrams());

        existing.setFoodName(nutrition.getFoodName());
        existing.setGrams(nutrition.getGrams());
        existing.setCalories(nutrition.getCalories());
        existing.setProtein(nutrition.getProtein());
        existing.setCarbs(nutrition.getCarbs());
        existing.setFat(nutrition.getFat());

        return toResponse(mealEntryRepository.save(existing));
    }

    public void deleteMeal(Long userId, Long mealId) {
        MealEntry existing = mealEntryRepository.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        mealEntryRepository.delete(existing);
    }

    private MealResponse toResponse(MealEntry entry) {
        return new MealResponse(
                entry.getId(),
                entry.getMealDate(),
                entry.getFoodName(),
                entry.getGrams(),
                entry.getCalories(),
                entry.getProtein(),
                entry.getCarbs(),
                entry.getFat()
        );
    }
}