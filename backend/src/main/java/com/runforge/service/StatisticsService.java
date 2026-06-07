package com.runforge.service;

import com.runforge.repository.MemberRepository;
import com.runforge.repository.PlanAssignmentRepository;
import com.runforge.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * StatisticsService — Aggregates dashboard statistics across all entities.
 *
 * <p>This service computes aggregated metrics for the dashboard, including:
 * <ul>
 *   <li>Total and active member counts</li>
 *   <li>Total training plan count</li>
 *   <li>Active and completed assignment counts</li>
 *   <li>Member distribution by running level (BEGINNER, INTERMEDIATE, ADVANCED)</li>
 * </ul>
 *
 * <p>All statistics are calculated at runtime using custom queries on the repositories.
 * Results are cached by TanStack Query on the frontend (staleTime: 30s).
 *
 * @see MemberRepository
 * @see TrainingPlanRepository
 * @see PlanAssignmentRepository
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MemberRepository memberRepository;
    private final PlanAssignmentRepository assignmentRepository;
    private final TrainingPlanRepository planRepository;

    /**
     * Computes aggregated dashboard statistics.
     *
     * <p>Returns a map with the following keys:
     * <ul>
     *   <li>{@code totalMembers}: Total count of all member records</li>
     *   <li>{@code activeMembers}: Count of members where active = true</li>
     *   <li>{@code totalPlans}: Total count of training plans</li>
     *   <li>{@code activeAssignments}: Count of assignments where completed = false</li>
     *   <li>{@code completedAssignments}: Count of assignments where completed = true</li>
     *   <li>{@code membersByLevel}: Map of (RunningLevel -> count) distribution</li>
     * </ul>
     *
     * @return map of statistics keyed by metric name
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Individual counts
        stats.put("totalMembers", memberRepository.count());
        stats.put("activeMembers", memberRepository.countActive());
        stats.put("totalPlans", planRepository.count());
        stats.put("activeAssignments", assignmentRepository.countActive());
        stats.put("completedAssignments", assignmentRepository.countCompleted());

        // Distribution by running level (active members only)
        Map<String, Long> levelDistribution = new HashMap<>();
        for (Object[] row : memberRepository.countByLevel()) {
            levelDistribution.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("membersByLevel", levelDistribution);

        return stats;
    }
}
