package com.fitapp.fitappbackend.service;

import com.fitapp.fitappbackend.dto.AuthResponse;
import com.fitapp.fitappbackend.dto.LoginRequest;
import com.fitapp.fitappbackend.dto.RegisterRequest;
import com.fitapp.fitappbackend.model.User;
import com.fitapp.fitappbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(false, "Email already exists", null, null);
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        User savedUser = userRepository.save(user);

        return new AuthResponse(true, "Registration successful", savedUser.getId(), savedUser.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new AuthResponse(false, "User not found", null, null);
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(request.getPassword())) {
            return new AuthResponse(false, "Invalid password", null, null);
        }

        return new AuthResponse(true, "Login successful", user.getId(), user.getUsername());
    }
}