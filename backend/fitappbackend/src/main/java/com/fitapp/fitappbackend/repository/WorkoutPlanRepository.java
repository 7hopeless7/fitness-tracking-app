package com.fitapp.fitappbackend.repository;

import com.fitapp.fitappbackend.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserIdOrderByIdDesc(Long userId);

    Optional<WorkoutPlan> findByIdAndUserId(Long id, Long userId);
}