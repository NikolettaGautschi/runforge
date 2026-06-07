package com.runforge.repository;

import com.runforge.model.PlanAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * PlanAssignmentRepository — Data access layer for PlanAssignment entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations plus
 * custom queries for plan assignment-specific operations:
 * <ul>
 *   <li>Finding assignments by member</li>
 *   <li>Finding assignments by training plan</li>
 *   <li>Counting active (incomplete) and completed assignments</li>
 * </ul>
 *
 * @see PlanAssignment
 */
public interface PlanAssignmentRepository extends JpaRepository<PlanAssignment, Long> {

    /**
     * Finds all assignments for a specific member.
     *
     * @param memberId the member's ID
     * @return list of assignments for the member
     */
    List<PlanAssignment> findByMemberId(Long memberId);

    /**
     * Finds all assignments for a specific training plan.
     *
     * @param planId the training plan's ID
     * @return list of assignments for the plan
     */
    List<PlanAssignment> findByTrainingPlanId(Long planId);

    /**
     * Counts active (incomplete) assignments.
     *
     * <p>An assignment is considered active if completed = false.
     *
     * @return count of active assignments
     */
    @Query("SELECT COUNT(a) FROM PlanAssignment a WHERE a.completed = false")
    long countActive();

    /**
     * Counts completed assignments.
     *
     * <p>An assignment is considered completed if completed = true.
     *
     * @return count of completed assignments
     */
    @Query("SELECT COUNT(a) FROM PlanAssignment a WHERE a.completed = true")
    long countCompleted();
}
