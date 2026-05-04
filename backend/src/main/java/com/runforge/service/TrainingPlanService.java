package com.runforge.service;

import com.runforge.model.TrainingPlan;
import com.runforge.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingPlanService {

    private final TrainingPlanRepository planRepository;

    public List<TrainingPlan> findAll() {
        return planRepository.findAll();
    }

    public TrainingPlan findById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainingsplan nicht gefunden: " + id));
    }

    public TrainingPlan create(TrainingPlan plan) {
        return planRepository.save(plan);
    }

    public TrainingPlan update(Long id, TrainingPlan updated) {
        TrainingPlan existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setLevel(updated.getLevel());
        existing.setDurationWeeks(updated.getDurationWeeks());
        existing.setWeeklyKm(updated.getWeeklyKm());
        return planRepository.save(existing);
    }

    public void delete(Long id) {
        planRepository.deleteById(id);
    }
}
