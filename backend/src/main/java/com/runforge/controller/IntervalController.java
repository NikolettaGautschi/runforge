package com.runforge.controller;

import com.runforge.model.Interval;
import com.runforge.service.IntervalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TrainingSetController — REST endpoints for training set management.
 *
 * <p>Provides CRUD operations for training sets (individual workout sessions) via HTTP:
 * <ul>
 *   <li>GET /api/plans/{planId}/sets — retrieve all sets for a plan</li>
 *   <li>GET /api/plans/{planId}/sets/type/{type} — retrieve sets by type (DISTANCE/DURATION)</li>
 *   <li>GET /api/sets/{id} — retrieve a specific set</li>
 *   <li>POST /api/plans/{planId}/sets — create new set</li>
 *   <li>PUT /api/sets/{id} — update existing set</li>
 *   <li>DELETE /api/sets/{id} — delete set</li>
 *   <li>POST /api/plans/{planId}/sets/reorder — reorder sets by ID list</li>
 * </ul>
 *
 * <p>CORS is enabled for http://localhost:3000 to support frontend proxying.
 * All request bodies are validated using Jakarta Validation annotations.
 *
 * @see IntervalService
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class IntervalController {

    private final IntervalService setService;

    /**
     * GET /api/plans/{planId}/sets — Retrieve all training sets for a plan.
     *
     * <p>Returns sets ordered by their sequence (order field).
     *
     * @param planId the training plan ID
     * @return list of training sets for the plan, ordered by sequence
     */
    @GetMapping("/api/plans/{planId}/sets")
    public List<Interval> getSetsByPlan(@PathVariable Long planId) {
        return setService.findByTrainingPlan(planId);
    }

    /**
     * GET /api/plans/{planId}/sets/type/{type} — Retrieve sets of a specific type.
     *
     * <p>Filters training sets by type (DISTANCE or DURATION) within a plan.
     *
     * @param planId the training plan ID
     * @param type the training set type (DISTANCE or DURATION)
     * @return list of matching training sets, ordered by sequence
     */
    @GetMapping("/api/plans/{planId}/sets/type/{type}")
    public List<Interval> getSetsByPlanAndType(
            @PathVariable Long planId,
            @PathVariable Interval.TargetType type
    ) {
        return setService.findByTrainingPlanAndType(planId, type);
    }

    /**
     * GET /api/sets/{id} — Retrieve a specific training set.
     *
     * @param id the training set's unique identifier
     * @return 200 OK with set data, or 404 if not found
     */
    @GetMapping("/api/sets/{id}")
    public ResponseEntity<Interval> getById(@PathVariable Long id) {
        return ResponseEntity.ok(setService.findById(id));
    }

    /**
     * POST /api/plans/{planId}/sets — Create a new training set.
     *
     * <p>Automatically assigns the next sequence order if not provided.
     * Validates that unit is compatible with type.
     *
     * @param planId the training plan ID (must exist)
     * @param set the training set dto (type, unit, quantity required)
     * @return 201 CREATED with the created set including auto-generated ID and order
     */
    @PostMapping("/api/plans/{planId}/sets")
    public ResponseEntity<Interval> createSet(
            @PathVariable Long planId,
            @Valid @RequestBody Interval set
    ) {
        Interval created = setService.create(planId, set);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/sets/{id} — Update an existing training set.
     *
     * <p>Validates unit/type compatibility when either is being changed.
     *
     * @param id the training set's ID (must exist)
     * @param set the training set dto with fields to update
     * @return 200 OK with the updated set
     */
    @PutMapping("/api/sets/{id}")
    public ResponseEntity<Interval> updateSet(
            @PathVariable Long id,
            @Valid @RequestBody Interval set
    ) {
        return ResponseEntity.ok(setService.update(id, set));
    }

    /**
     * DELETE /api/sets/{id} — Delete a training set.
     *
     * @param id the training set's ID
     * @return 204 NO CONTENT
     */
    @DeleteMapping("/api/sets/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable Long id) {
        setService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/plans/{planId}/sets/reorder — Reorder training sets.
     *
     * <p>Accepts a list of training set IDs in the desired order and reassigns
     * their order values accordingly. Useful for drag-and-drop UI operations.
     *
     * <p>Request body example:
     * <pre>
     * {
     *   "setIds": [3, 1, 2]
     * }
     * </pre>
     *
     * @param planId the training plan ID
     * @param request dto containing list of set IDs in new order
     * @return 200 OK when reordering succeeds
     */
    @PostMapping("/api/plans/{planId}/sets/reorder")
    public ResponseEntity<Void> reorderSets(
            @PathVariable Long planId,
            @RequestBody ReorderRequest request
    ) {
        setService.reorder(planId, request.getSetIds());
        return ResponseEntity.ok().build();
    }

    /**
     * ReorderRequest — DTO for set reordering request.
     *
     * <p>Provides a list of training set IDs in the desired sequence order.
     */
    @Getter
    @Setter
    public static final class ReorderRequest {
        /**
         * List of training set IDs in desired order.
         */
        private List<Long> setIds;
    }
}

