package com.runforge.controller;

import com.runforge.model.TrainingPlan;
import com.runforge.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TrainingPlanController — REST endpoints for training plan management.
 *
 * <p>Provides CRUD operations for training plans via HTTP:
 * <ul>
 *   <li>GET /api/plans — retrieve all plans</li>
 *   <li>GET /api/plans/{id} — retrieve a specific plan</li>
 *   <li>POST /api/plans — create new plan</li>
 *   <li>PUT /api/plans/{id} — update existing plan</li>
 *   <li>DELETE /api/plans/{id} — delete plan</li>
 * </ul>
 *
 * <p>CORS is enabled for http://localhost:3000 to support frontend proxying.
 * All request bodies are validated using Jakarta Validation annotations.
 *
 * @see TrainingService
 */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TrainingPlanController {

    private final TrainingService planService;

    /**
     * GET /api/plans — Retrieve all training plans.
     *
     * @return list of all training plans
     */
    @GetMapping
    public List<TrainingPlan> getAll() {
        return planService.findAll();
    }

    /**
     * GET /api/plans/{id} — Retrieve a specific plan by ID.
     *
     * @param id the plan's unique identifier
     * @return 200 OK with plan data, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlan> getById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.findById(id));
    }

    /**
     * POST /api/plans — Create a new training plan.
     *
     * @param plan the plan dto (name, level required)
     * @return 201 CREATED with the created plan including auto-generated ID
     */
    @PostMapping
    public ResponseEntity<TrainingPlan> create(@Valid @RequestBody TrainingPlan plan) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.create(plan));
    }

    /**
     * PUT /api/plans/{id} — Update an existing training plan.
     *
     * @param id the plan's ID (must exist)
     * @param plan the plan dto with fields to update
     * @return 200 OK with the updated plan
     */
    @PutMapping("/{id}")
    public ResponseEntity<TrainingPlan> update(@PathVariable Long id, @Valid @RequestBody TrainingPlan plan) {
        return ResponseEntity.ok(planService.update(id, plan));
    }

    /**
     * DELETE /api/plans/{id} — Delete a training plan.
     *
     * <p>Performs a hard delete (unlike member deletion which uses soft delete).
     *
     * @param id the plan's ID
     * @return 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
