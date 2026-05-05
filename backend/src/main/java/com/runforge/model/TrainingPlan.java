package com.runforge.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    @Positive
    private int durationWeeks;

    @Positive
    private int weeklyKm;

    private LocalDate createdDate = LocalDate.now();

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PlanAssignment> assignments;
}
