package com.runforge.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Member — Represents a running group member.
 *
 * <p>This entity models a member of the running group with personal information,
 * running level, and tracking of active status. Soft deletion is supported via
 * the {@code active} boolean flag instead of hard deletion.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id}: Unique identifier (auto-generated)</li>
 *   <li>{@code firstName}: Member's first name (required)</li>
 *   <li>{@code lastName}: Member's last name (required)</li>
 *   <li>{@code email}: Email address (unique, required)</li>
 *   <li>{@code phone}: Phone number (optional)</li>
 *   <li>{@code level}: Running proficiency level (BEGINNER, INTERMEDIATE, ADVANCED)</li>
 *   <li>{@code joinedDate}: Date member joined the group (defaults to today)</li>
 *   <li>{@code active}: Soft-delete flag (defaults to true)</li>
 *   <li>{@code assignments}: One-to-many relationship to PlanAssignment</li>
 * </ul>
 *
 * <p>Validation constraints:
 * <ul>
 *   <li>firstName and lastName must not be blank</li>
 *   <li>email must be valid email format and unique across members</li>
 *   <li>level defaults to BEGINNER if not specified</li>
 * </ul>
 *
 * @see RunningLevel
 * @see PlanAssignment
 */
@Entity
@Data
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @Column(unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private RunningLevel level = RunningLevel.BEGINNER;

    private LocalDate joinedDate = LocalDate.now();

    /**
     * Soft-delete flag. When false, the member is considered inactive/deleted
     * but the record remains in the database for audit purposes.
     */
    private boolean active = true;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PlanAssignment> assignments;

    /**
     * RunningLevel — Represents the proficiency level of a running group member.
     *
     * <p>Three tiers of running experience to match training plans and difficulty levels.
     */
    public enum RunningLevel {
        /** Beginner level — new to running or returning after a break */
        BEGINNER,
        /** Intermediate level — regular runner with some experience */
        INTERMEDIATE,
        /** Advanced level — experienced runner ready for challenging plans */
        ADVANCED
    }
}
