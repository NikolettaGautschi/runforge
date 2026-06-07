package com.runforge.controller;

import com.runforge.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * StatisticsController — REST endpoint for aggregated dashboard statistics.
 *
 * <p>Provides a single aggregated endpoint to fetch all dashboard metrics:
 * <ul>
 *   <li>GET /api/stats/dashboard — retrieve aggregated statistics</li>
 * </ul>
 *
 * <p>The response includes total/active member counts, plan counts, assignment status,
 * and member distribution by running level.
 *
 * <p>CORS is enabled for http://localhost:3000 to support frontend proxying.
 *
 * @see StatisticsService
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * GET /api/stats/dashboard — Retrieve aggregated dashboard statistics.
     *
     * <p>Returns a map with keys:
     * <ul>
     *   <li>totalMembers: Total member count</li>
     *   <li>activeMembers: Count of active members</li>
     *   <li>totalPlans: Total plan count</li>
     *   <li>activeAssignments: Count of incomplete assignments</li>
     *   <li>completedAssignments: Count of completed assignments</li>
     *   <li>membersByLevel: Map of (level -> count) distribution</li>
     * </ul>
     *
     * @return 200 OK with statistics map
     */
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return statisticsService.getDashboardStats();
    }
}
