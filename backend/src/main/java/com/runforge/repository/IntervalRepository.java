package com.runforge.repository;

import com.runforge.model.Interval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * IntervalRepository — Data access layer for Interval entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations plus
 * custom queries for training set-specific operations:
 * <ul>
 *   <li>Finding intervals by training plan</li>
 *   <li>Finding intervals ordered by sequence</li>
 *   <li>Finding intervals by type (distance or duration)</li>
 *   <li>Counting intervals in a plan</li>
 * </ul>
 *
 * @see Interval
 */
public interface IntervalRepository extends JpaRepository<Interval, Long> {

    /**
     * Finds all training intervals belonging to a specific training plan,
     * ordered by their sequence (order field).
     *
     * @param trainingId the training plan ID
     * @return list of training intervals for the plan, ordered by sequence
     */
    @Query("SELECT i FROM Interval i WHERE i.training.id = :trainingId ORDER BY i.seqnr ASC")
    List<Interval> findByTrainingIdOrderByOrder(@Param("trainingId") Long trainingId);

    /**
     * Finds all training intervals of a specific type (DISTANCE or DURATION)
     * belonging to a training plan.
     *
     * @param trainingId the training plan ID
     * @param type the training set type
     * @return list of matching training intervals
     */
    @Query("SELECT i FROM Interval i WHERE i.training.id = :trainingId AND i.type = :type ORDER BY i.seqnr ASC")
    List<Interval> findByTrainingIdAndType(@Param("trainingId") Long trainingId, @Param("type") Interval.TargetType type);

    /**
     * Counts the number of training intervals in a specific plan.
     *
     * @param trainingId the training plan ID
     * @return count of training intervals in the plan
     */
    @Query("SELECT COUNT(i) FROM Interval i WHERE i.training.id = :trainingId")
    long countByTrainingId(@Param("trainingId") Long trainingId);

    /**
     * Finds the maximum sequence order value in a training plan.
     * Useful for determining the next order number when adding new intervals.
     *
     * @param trainingId the training plan ID
     * @return the highest order value, or null if no intervals exist
     */
    @Query("SELECT MAX(i.seqnr) FROM Interval i WHERE i.training.id = :trainingId")
    Integer findMaxOrderByTrainingId(@Param("trainingId") Long trainingId);
}

