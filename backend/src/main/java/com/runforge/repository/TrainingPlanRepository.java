package com.runforge.repository;

import com.runforge.model.TrainingPlan;
import com.runforge.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findByLevel(Member.RunningLevel level);
}
