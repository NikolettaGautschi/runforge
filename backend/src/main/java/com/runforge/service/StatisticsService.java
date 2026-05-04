package com.runforge.service;

import com.runforge.repository.MemberRepository;
import com.runforge.repository.PlanAssignmentRepository;
import com.runforge.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MemberRepository memberRepository;
    private final PlanAssignmentRepository assignmentRepository;
    private final TrainingPlanRepository planRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalMembers", memberRepository.count());
        stats.put("activeMembers", memberRepository.countActive());
        stats.put("totalPlans", planRepository.count());
        stats.put("activeAssignments", assignmentRepository.countActive());
        stats.put("completedAssignments", assignmentRepository.countCompleted());

        // Verteilung nach Level
        Map<String, Long> levelDistribution = new HashMap<>();
        for (Object[] row : memberRepository.countByLevel()) {
            levelDistribution.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("membersByLevel", levelDistribution);

        return stats;
    }
}
