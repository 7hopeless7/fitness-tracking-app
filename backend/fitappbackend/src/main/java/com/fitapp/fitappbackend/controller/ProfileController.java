package com.fitapp.fitappbackend.controller;

import com.fitapp.fitappbackend.dto.ProfileRequest;
import com.fitapp.fitappbackend.dto.ProfileResponse;
import com.fitapp.fitappbackend.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse getProfile(@RequestParam Long userId) {
        return profileService.getProfile(userId);
    }

    @PutMapping
    public ProfileResponse updateProfile(
            @RequestParam Long userId,
            @RequestBody ProfileRequest request
    ) {
        return profileService.updateProfile(userId, request);
    }
}