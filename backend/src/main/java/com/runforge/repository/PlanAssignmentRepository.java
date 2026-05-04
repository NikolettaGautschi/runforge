package com.runforge.repository;

import com.runforge.model.PlanAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanAssignmentRepository extends JpaRepository<PlanAssignment, Long> {

    List<PlanAssignment> findByMemberId(Long memberId);

    List<PlanAssignment> findByTrainingPlanId(Long planId);

    @Query("SELECT COUNT(a) FROM PlanAssignment a WHERE a.completed = false")
    long countActive();

    @Query("SELECT COUNT(a) FROM PlanAssignment a WHERE a.completed = true")
    long countCompleted();
}
