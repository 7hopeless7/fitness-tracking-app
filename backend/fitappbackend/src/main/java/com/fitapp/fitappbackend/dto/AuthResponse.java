package com.fitapp.fitappbackend.dto;

public class AuthResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message, Long userId, String username) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}