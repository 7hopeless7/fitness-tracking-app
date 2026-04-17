package com.fitapp.fitappbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitapp.fitappbackend.dto.CaloriesRecommendationResponse;
import com.fitapp.fitappbackend.dto.FoodRequest;
import com.fitapp.fitappbackend.dto.FoodResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NutritionService {

    @Value("${deepseek.api.key}")
    private String deepseekApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FoodResponse analyzeFood(FoodRequest request) {
        return analyzeFood(request.getFoodName(), request.getGrams());
    }

    public FoodResponse analyzeFood(String foodName, double grams) {
        try {
            String prompt = """
                You are a nutrition assistant.
                Estimate nutrition for this food and return ONLY valid JSON.
                
                Food: %s
                Grams: %.2f
                
                Return exactly this JSON format:
                {
                  "foodName": "string",
                  "grams": number,
                  "calories": number,
                  "protein": number,
                  "carbs": number,
                  "fat": number
                }
                """.formatted(foodName, grams);

            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Return only valid JSON.");

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            body.put("messages", List.of(systemMessage, userMessage));
            body.put("response_format", Map.of("type", "json_object"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepseekApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.deepseek.com/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            return objectMapper.readValue(content, FoodResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze food: " + e.getMessage(), e);
        }
    }
    public double recommendCalories(int age, double weightKg, String activityLevel, String goal) {
        try {
            String prompt = """
                You are a nutrition coach.
                Recommend daily calories for this user and return ONLY valid JSON.

                Age: %d
                Weight (kg): %.2f
                Activity level: %s
                Goal: %s

                Return exactly this JSON format:
                {
                  "recommendedCalories": number
                }
                """.formatted(age, weightKg, activityLevel, goal);

            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Return only valid JSON.");

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            body.put("messages", List.of(systemMessage, userMessage));
            body.put("response_format", Map.of("type", "json_object"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepseekApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.deepseek.com/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            CaloriesRecommendationResponse recommendation = objectMapper.readValue(content, CaloriesRecommendationResponse.class);

            if (recommendation.getRecommendedCalories() == null) {
                throw new RuntimeException("Missing recommendedCalories in DeepSeek response");
            }

            return recommendation.getRecommendedCalories();
        } catch (Exception e) {
            throw new RuntimeException("Failed to recommend calories: " + e.getMessage(), e);
        }
    }
}