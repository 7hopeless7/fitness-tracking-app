package com.fitapp.fitappbackend.dto;

import java.util.List;
import java.util.Map;

public class ExerciseLibraryResponse {
    private Map<String, List<String>> categories;

    public ExerciseLibraryResponse() {
    }

    public ExerciseLibraryResponse(Map<String, List<String>> categories) {
        this.categories = categories;
    }

    public Map<String, List<String>> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, List<String>> categories) {
        this.categories = categories;
    }
}