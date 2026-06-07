package com.runforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * TrainingSet — Represents a single training session or workout segment.
 *
 * <p>This entity models an individual training session that belongs to a {@link TrainingPlan}.
 * Each training set specifies an exercise type (distance-based or duration-based) with
 * a quantity and unit of measurement. Sets are ordered sequentially within a plan.
 *
 * <p>Design: TrainingSet supports two mutually exclusive workout types:
 * <ul>
 *   <li><strong>Distance-based</strong>: Quantity measured in KM (kilometers) or M (meters)</li>
 *   <li><strong>Duration-based</strong>: Quantity measured in MIN (minutes) or SEC (seconds)</li>
 * </ul>
 *
 * <p>Example use cases:
 * <ul>
 *   <li>Distance set: "Run 5 KM" (type=DISTANCE, quantity=5, unit=KM)</li>
 *   <li>Duration set: "Run for 30 MIN" (type=DURATION, quantity=30, unit=MIN)</li>
 *   <li>Sprint set: "Run for 30 SEC" (type=DURATION, quantity=30, unit=SEC)</li>
 * </ul>
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id}: Unique identifier (auto-generated)</li>
 *   <li>{@code type}: Workout type (DISTANCE or DURATION) — mutually exclusive</li>
 *   <li>{@code unit}: Unit of measurement (must be compatible with type)</li>
 *   <li>{@code order}: Sequence position within the training plan (1, 2, 3...)</li>
 *   <li>{@code quantity}: Numerical value for the exercise (positive required)</li>
 *   <li>{@code createdDate}: Date when the training set was created</li>
 *   <li>{@code training}: Reference to parent {@link TrainingPlan}</li>
 * </ul>
 *
 * <p>Relationships:
 * <ul>
 *   <li>Many-to-One with TrainingPlan (required, lazy loaded)</li>
 * </ul>
 *
 * <p>Validation:
 * <ul>
 *   <li>Type and unit are required and stored as STRING enum values</li>
 *   <li>Quantity must be positive (via {@code @Positive})</li>
 *   <li>Unit must be compatible with type via {@link TargetUnit#isCompatibleWith(TargetType)}</li>
 * </ul>
 *
 * @see TrainingPlan
 * @see TargetType
 * @see TargetUnit
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "training_interval")
public class Interval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Workout type: either distance-based or time-based exercise.
     * Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    /**
     * Unit of measurement. Must be compatible with the selected type:
     * - For DISTANCE: KM or M
     * - For DURATION: MIN or SEC
     * Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetUnit targetUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    /**
     * Sequence order within the training plan.
     * Used to determine the execution order of training sets.
     * Cannot be null.
     */
    @Column(nullable = false)
    private Integer seqnr;

    /**
     * Quantity/magnitude of the exercise.
     * For distance sets: numerical value in KM or M
     * For duration sets: numerical value in MIN or SEC
     * Must be positive.
     */
    @Positive
    private Double targetValue;

    /**
     * Date when this training set was created.
     * Defaults to the current date.
     */
    private LocalDate createdDate = LocalDate.now();

    /**
     * Reference to the parent training plan.
     * Many training sets can belong to one plan.
     * Cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = false)
    private TrainingPlan training;


    public enum Type{
        WARMUP, RUN, REST, COOLDOWN
    }

    /**
     * Type — Represents the category of workout exercise.
     *
     * <p>Two mutually exclusive types:
     * <ul>
     *   <li>{@code DISTANCE}: Workout defined by distance to cover</li>
     *   <li>{@code DURATION}: Workout defined by time duration</li>
     * </ul>
     *
     * <p>Each type has compatible units (see {@link TargetUnit}).
     */
    public enum TargetType {
        /**
         * Distance-based workout: quantity represents distance to cover.
         * Compatible units: KM, M
         */
        DISTANCE,

        /**
         * Duration-based workout: quantity represents time duration.
         * Compatible units: MIN, SEC
         */
        DURATION
    }

    /**
     * Unit — Represents the unit of measurement for a training set quantity.
     *
     * <p>Four units supporting two categories:
     * <ul>
     *   <li><strong>Distance units</strong>: KM (kilometers), M (meters)</li>
     *   <li><strong>Duration units</strong>: MIN (minutes), SEC (seconds)</li>
     * </ul>
     *
     * <p>Each unit is compatible with exactly one {@link TargetType}. Use
     * {@link #isCompatibleWith(TargetType)} to validate compatibility before
     * saving or updating a training set.
     */
    public enum TargetUnit {
        // ===== DISTANCE UNITS =====
        /**
         * Kilometers — distance unit for large-scale workouts.
         * Compatible with: {@link TargetType#DISTANCE}
         */
        KM,

        /**
         * Meters — distance unit for smaller distances or sprints.
         * Compatible with: {@link TargetType#DISTANCE}
         */
        M,

        // ===== DURATION UNITS =====
        /**
         * Minutes — time unit for standard workout durations.
         * Compatible with: {@link TargetType#DURATION}
         */
        MIN,

        /**
         * Seconds — time unit for high-intensity or very short intervals.
         * Compatible with: {@link TargetType#DURATION}
         */
        SEC;

        /**
         * Validates whether this unit is compatible with the given training type.
         *
         * <p>Performs a type-safe check using switch expression:
         * <ul>
         *   <li>DISTANCE accepts: KM, M</li>
         *   <li>DURATION accepts: MIN, SEC</li>
         * </ul>
         *
         * <p>Usage example:
         * <pre>
         * if (Unit.KM.isCompatibleWith(Type.DISTANCE)) {
         *     // Valid: KM is compatible with DISTANCE
         * }
         * </pre>
         *
         * @param type the training {@link TargetType} to validate against
         * @return true if this unit is compatible with the given type; false otherwise
         */
        public boolean isCompatibleWith(TargetType type) {
            return switch (type) {
                case DISTANCE -> this == KM || this == M;
                case DURATION -> this == MIN || this == SEC;
            };
        }
    }
}
