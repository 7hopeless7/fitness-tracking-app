package com.fitapp.fitappbackend.service;

import com.fitapp.fitappbackend.dto.ProfileRequest;
import com.fitapp.fitappbackend.dto.ProfileResponse;
import com.fitapp.fitappbackend.model.User;
import com.fitapp.fitappbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final NutritionService nutritionService;

    public ProfileService(UserRepository userRepository, NutritionService nutritionService) {
        this.userRepository = userRepository;
        this.nutritionService = nutritionService;
    }

    public ProfileResponse getProfile(Long userId) {
        User user = getUserById(userId);
        return toResponse(user);
    }

    public ProfileResponse updateProfile(Long userId, ProfileRequest request) {
        User user = getUserById(userId);

        user.setAge(request.getAge());
        user.setWeightKg(request.getWeightKg());
        user.setActivityLevel(request.getActivityLevel());
        user.setGoal(request.getGoal());

        if (request.getAge() != null
                && request.getWeightKg() != null
                && request.getActivityLevel() != null
                && request.getGoal() != null) {
            double recommendedCalories = nutritionService.recommendCalories(
                    request.getAge(),
                    request.getWeightKg(),
                    request.getActivityLevel(),
                    request.getGoal()
            );
            user.setRecommendedCalories(recommendedCalories);
        }

        return toResponse(userRepository.save(user));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ProfileResponse toResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAge(),
                user.getWeightKg(),
                user.getActivityLevel(),
                user.getGoal(),
                user.getRecommendedCalories()
        );
    }
}