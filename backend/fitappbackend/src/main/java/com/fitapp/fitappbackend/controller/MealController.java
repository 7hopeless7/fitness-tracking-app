package com.fitapp.fitappbackend.controller;

import com.fitapp.fitappbackend.dto.MealRequest;
import com.fitapp.fitappbackend.dto.MealResponse;
import com.fitapp.fitappbackend.service.MealService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    public List<MealResponse> getMeals(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return mealService.getMealsForDate(userId, date);
    }

    @PostMapping
    public MealResponse addMeal(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody MealRequest request
    ) {
        return mealService.addMeal(userId, date, request);
    }

    @PutMapping("/{mealId}")
    public MealResponse updateMeal(
            @PathVariable Long mealId,
            @RequestParam Long userId,
            @RequestBody MealRequest request
    ) {
        return mealService.updateMeal(userId, mealId, request);
    }

    @DeleteMapping("/{mealId}")
    public void deleteMeal(
            @PathVariable Long mealId,
            @RequestParam Long userId
    ) {
        mealService.deleteMeal(userId, mealId);
    }
}