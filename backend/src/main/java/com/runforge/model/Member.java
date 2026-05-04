package com.runforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    private boolean active = true;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanAssignment> assignments;

    public enum RunningLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
