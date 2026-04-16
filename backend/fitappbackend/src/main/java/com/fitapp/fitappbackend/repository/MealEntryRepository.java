package com.fitapp.fitappbackend.repository;

import com.fitapp.fitappbackend.model.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    List<MealEntry> findByUserIdAndMealDateOrderByIdDesc(Long userId, LocalDate mealDate);

    Optional<MealEntry> findByIdAndUserId(Long id, Long userId);
}