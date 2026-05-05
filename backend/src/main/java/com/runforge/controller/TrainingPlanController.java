package com.runforge.controller;

import com.runforge.model.TrainingPlan;
import com.runforge.service.TrainingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v01/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TrainingPlanController {

    private final TrainingPlanService planService;

    @GetMapping
    public List<TrainingPlan> getAll() {
        return planService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlan> getById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TrainingPlan> create(@Valid @RequestBody TrainingPlan plan) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.create(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingPlan> update(@PathVariable Long id, @Valid @RequestBody TrainingPlan plan) {
        return ResponseEntity.ok(planService.update(id, plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
