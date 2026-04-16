package com.fitapp.fitappbackend.controller;

import com.fitapp.fitappbackend.dto.FoodRequest;
import com.fitapp.fitappbackend.dto.FoodResponse;
import com.fitapp.fitappbackend.service.NutritionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition")
@CrossOrigin(origins = "*")
public class NutritionController {

    private final NutritionService nutritionService;

    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @PostMapping("/analyze")
    public FoodResponse analyze(@RequestBody FoodRequest request) {
        return nutritionService.analyzeFood(request);
    }
}