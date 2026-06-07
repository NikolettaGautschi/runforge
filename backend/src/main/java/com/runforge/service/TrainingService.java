package com.runforge.service;

import com.runforge.model.TrainingPlan;
import com.runforge.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TrainingService — Business logic for managing training plans.
 *
 * <p>This service provides CRUD operations for training plans with transactional support.
 * Training plans can be assigned to members via PlanAssignment and are organized by
 * running level (BEGINNER, INTERMEDIATE, ADVANCED).
 *
 * <p>All methods use {@link TrainingPlanRepository} for data access and throw
 * {@link RuntimeException} when entities are not found.
 *
 * @see TrainingPlan
 * @see TrainingPlanRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingService {

    private final TrainingPlanRepository planRepository;

    /**
     * Retrieves all training plans.
     *
     * @return list of all training plans
     */
    public List<TrainingPlan> findAll() {
        return planRepository.findAll();
    }

    /**
     * Retrieves a specific training plan by ID.
     *
     * @param id the plan's unique identifier
     * @return the training plan if found
     * @throws RuntimeException if plan not found
     */
    public TrainingPlan findById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainingsplan nicht gefunden: " + id));
    }

    /**
     * Creates a new training plan.
     *
     * @param plan the plan to create (name, level required)
     * @return the created plan with auto-generated ID and createdDate
     */
    public TrainingPlan create(TrainingPlan plan) {
        return planRepository.save(plan);
    }

    /**
     * Updates an existing training plan by ID.
     *
     * <p>Merges provided fields into the existing plan record. All fields are updatable.
     *
     * @param id the plan's ID (must exist)
     * @param updated the plan dto with fields to update
     * @return the updated training plan
     * @throws RuntimeException if plan not found
     */
    public TrainingPlan update(Long id, TrainingPlan updated) {
        TrainingPlan existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setLevel(updated.getLevel());
        return planRepository.save(existing);
    }

    /**
     * Deletes a training plan by ID (hard delete).
     *
     * <p>Note: This is a hard delete, unlike member deletion which uses soft delete.
     *
     * @param id the plan's ID
     */
    public void delete(Long id) {
        planRepository.deleteById(id);
    }
}
