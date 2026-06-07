package com.runforge.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * PlanAssignment — Represents an assignment of a training plan to a member.
 *
 * <p>This join-table entity tracks when a member is assigned to a training plan,
 * including start/end dates and completion status. It enables many-to-many
 * relationships between members and training plans.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id}: Unique identifier (auto-generated)</li>
 *   <li>{@code member}: Reference to the assigned member (required)</li>
 *   <li>{@code trainingPlan}: Reference to the training plan (required)</li>
 *   <li>{@code startDate}: Date when the plan starts</li>
 *   <li>{@code endDate}: Date when the plan ends</li>
 *   <li>{@code completed}: Whether the plan has been completed (defaults to false)</li>
 * </ul>
 *
 * <p>Relationships:
 * <ul>
 *   <li>Many-to-One with Member (eager + lazy fetch)</li>
 *   <li>Many-to-One with TrainingPlan (eager + lazy fetch)</li>
 * </ul>
 *
 * @see Member
 * @see TrainingPlan
 */
@Entity
@Data
@NoArgsConstructor
public class PlanAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The member to whom this plan is assigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * The training plan that is assigned to the member.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_plan_id", nullable = false)
    private TrainingPlan trainingPlan;

    /**
     * Start date of the plan assignment.
     */
    private LocalDate startDate;

    /**
     * End date of the plan assignment.
     */
    private LocalDate endDate;

    /**
     * Flag indicating whether the member has completed this plan.
     */
    private boolean completed = false;
}
