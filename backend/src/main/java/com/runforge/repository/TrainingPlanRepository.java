package com.runforge.repository;

import com.runforge.model.TrainingPlan;
import com.runforge.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * TrainingPlanRepository — Data access layer for TrainingPlan entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations plus
 * custom queries for training plan-specific operations:
 * <ul>
 *   <li>Finding plans by target running level</li>
 * </ul>
 *
 * @see TrainingPlan
 */
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {

    /**
     * Finds all training plans targeting a specific running level.
     *
     * @param level the running level to filter by (BEGINNER, INTERMEDIATE, ADVANCED)
     * @return list of plans for the given level
     */
    List<TrainingPlan> findByLevel(Member.RunningLevel level);
}
