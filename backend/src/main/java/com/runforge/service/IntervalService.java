package com.runforge.service;

import com.runforge.model.Interval;
import com.runforge.model.TrainingPlan;
import com.runforge.repository.IntervalRepository;
import com.runforge.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TrainingSetService — Business logic for managing training sets (workout sessions).
 *
 * <p>This service provides CRUD operations for training sets with additional features:
 * <ul>
 *   <li>Validation of unit/type compatibility before persistence</li>
 *   <li>Automatic sequence ordering when adding new sets</li>
 *   <li>Retrieval of sets ordered by sequence</li>
 *   <li>Type-based filtering (distance vs. duration workouts)</li>
 *   <li>Transactional support for data consistency</li>
 * </ul>
 *
 * <p>Training sets are always part of a {@link TrainingPlan}. Operations validate
 * that the parent plan exists before proceeding.
 *
 * @see Interval
 * @see IntervalRepository
 * @see TrainingPlanRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
public class IntervalService {

    private final IntervalRepository setRepository;
    private final TrainingPlanRepository planRepository;

    /**
     * Retrieves all training sets for a specific training plan, ordered by sequence.
     *
     * @param trainingId the training plan ID
     * @return list of training sets ordered by sequence (order field)
     * @throws RuntimeException if training plan not found
     */
    public List<Interval> findByTrainingPlan(Long trainingId) {
        validateTrainingPlanExists(trainingId);
        return setRepository.findByTrainingIdOrderByOrder(trainingId);
    }

    /**
     * Retrieves a specific training set by ID.
     *
     * @param id the training set's unique identifier
     * @return the training set if found
     * @throws RuntimeException if training set not found
     */
    public Interval findById(Long id) {
        return setRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainingsset nicht gefunden: " + id));
    }

    /**
     * Retrieves all training sets of a specific type (DISTANCE or DURATION)
     * belonging to a training plan.
     *
     * @param trainingId the training plan ID
     * @param type the training set type (DISTANCE or DURATION)
     * @return list of training sets of the specified type, ordered by sequence
     * @throws RuntimeException if training plan not found
     */
    public List<Interval> findByTrainingPlanAndType(Long trainingId, Interval.TargetType type) {
        validateTrainingPlanExists(trainingId);
        return setRepository.findByTrainingIdAndType(trainingId, type);
    }

    /**
     * Creates a new training set with automatic sequence ordering.
     *
     * <p>Validates that:
     * <ul>
     *   <li>Parent training plan exists</li>
     *   <li>Unit is compatible with the specified type</li>
     *   <li>Quantity is positive</li>
     * </ul>
     *
     * If no order is provided, automatically assigns the next sequential order number.
     *
     * @param trainingId the parent training plan ID
     * @param interval the set to create (type, unit, quantity required)
     * @return the created training set with auto-generated ID and order
     * @throws RuntimeException if training plan not found or unit/type mismatch
     */
    public Interval create(Long trainingId, Interval interval) {
        // Validate parent plan exists
        TrainingPlan plan = planRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Trainingsplan nicht gefunden: " + trainingId));

        // Validate unit/type compatibility
        if (!interval.getTargetUnit().isCompatibleWith(interval.getTargetType())) {
            throw new RuntimeException(
                    String.format("Einheit %s ist nicht kompatibel mit Typ %s",
                            interval.getTargetUnit(), interval.getTargetType())
            );
        }

        // Auto-assign next order if not provided
        if (interval.getSeqnr() == null || interval.getSeqnr() <= 0) {
            Integer maxOrder = setRepository.findMaxOrderByTrainingId(trainingId);
            interval.setSeqnr((maxOrder != null ? maxOrder : 0) + 1);
        }

        // Link to training plan and save
        interval.setTraining(plan);
        return setRepository.save(interval);
    }

    /**
     * Updates an existing training set by ID.
     *
     * <p>Validates unit/type compatibility before updating. All fields are updatable.
     *
     * @param id the training set's ID (must exist)
     * @param updated the training set dto with fields to update
     * @return the updated training set
     * @throws RuntimeException if training set not found or validation fails
     */
    public Interval update(Long id, Interval updated) {
        Interval existing = findById(id);

        // Validate unit/type compatibility if either type or unit is being changed
        if (updated.getTargetType() != null && updated.getTargetUnit() != null) {
            if (!updated.getTargetUnit().isCompatibleWith(updated.getTargetType())) {
                throw new RuntimeException(
                        String.format("Einheit %s ist nicht kompatibel mit Typ %s",
                                updated.getTargetUnit(), updated.getTargetType())
                );
            }
        }

        // Update fields
        if (updated.getTargetType() != null) existing.setTargetType(updated.getTargetType());
        if (updated.getTargetUnit() != null) existing.setTargetUnit(updated.getTargetUnit());
        if (updated.getSeqnr() != null) existing.setSeqnr(updated.getSeqnr());
        if (updated.getTargetValue() != null) existing.setTargetValue(updated.getTargetValue());

        return setRepository.save(existing);
    }

    /**
     * Deletes a training set by ID.
     *
     * @param id the training set's ID
     * @throws RuntimeException if training set not found
     */
    public void delete(Long id) {
        Interval set = findById(id);
        setRepository.delete(set);
    }

    /**
     * Reorders training sets within a training plan.
     *
     * <p>Reassigns order values to sets to match their position in the provided list.
     * Useful when user drags/drops or manually reorders training sets.
     *
     * @param trainingId the training plan ID
     * @param setIds list of training set IDs in desired order
     * @throws RuntimeException if any set not found or belongs to different plan
     */
    public void reorder(Long trainingId, List<Long> setIds) {
        validateTrainingPlanExists(trainingId);

        for (int i = 0; i < setIds.size(); i++) {
            Interval set = findById(setIds.get(i));

            // Verify set belongs to this training plan
            if (!set.getTraining().getId().equals(trainingId)) {
                throw new RuntimeException(
                        String.format("Set %d gehört nicht zu Plan %d", setIds.get(i), trainingId)
                );
            }

            set.setSeqnr(i + 1);
            setRepository.save(set);
        }
    }

    /**
     * Counts training sets in a specific training plan.
     *
     * @param trainingId the training plan ID
     * @return count of training sets in the plan
     * @throws RuntimeException if training plan not found
     */
    public long countByTrainingPlan(Long trainingId) {
        validateTrainingPlanExists(trainingId);
        return setRepository.countByTrainingId(trainingId);
    }

    /**
     * Internal helper: Validates that a training plan exists.
     *
     * @param trainingId the training plan ID to check
     * @throws RuntimeException if training plan not found
     */
    private void validateTrainingPlanExists(Long trainingId) {
        if (!planRepository.existsById(trainingId)) {
            throw new RuntimeException("Trainingsplan nicht gefunden: " + trainingId);
        }
    }
}

