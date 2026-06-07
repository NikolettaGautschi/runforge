package com.runforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TrainingPlan — Represents a structured training program.
 *
 * <p>This entity models a training plan that can be assigned to one or more members.
 * Each plan targets a specific running level and includes metadata about duration
 * and weekly distance goals.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id}: Unique identifier (auto-generated)</li>
 *   <li>{@code name}: Name of the plan (required, e.g., "5km Beginner Plan")</li>
 *   <li>{@code description}: Detailed plan description (optional)</li>
 *   <li>{@code level}: Target running level (BEGINNER, INTERMEDIATE, ADVANCED)</li>
 *   <li>{@code createdDate}: Date the plan was created (defaults to today)</li>
 *   <li>{@code assignments}: One-to-many relationship to PlanAssignment</li>
 *   <li>{@code sets}: One-to-many collection of training sets/sessions</li>
 * </ul>
 *
 * <p>Additional metadata:
 * <ul>
 *   <li>{@code durationWeeks}: Total duration of the plan in weeks</li>
 *   <li>{@code weeklyKm}: Target weekly distance in kilometers</li>
 * </ul>
 *
 * @see Member
 * @see PlanAssignment
 * @see Interval
 */
@Entity
@Data
@NoArgsConstructor
public class TrainingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Member.RunningLevel level;

    private LocalDate createdDate = LocalDate.now();

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL)
    private List<PlanAssignment> assignments;

    /**
     * Training sets/sessions that make up this plan, ordered by sequence.
     */
    @OneToMany(mappedBy = "training",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("seqnr ASC")
    private List<Interval> intervals = new ArrayList<>();
}
