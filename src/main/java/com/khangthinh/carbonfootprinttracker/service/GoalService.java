package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoalService {
    Page<Goal> getUserGoals(String username, Pageable pageable);

    Goal createGoal(String username, Goal goalRequest);

    Goal updateGoalProgress(Long goalId, Double addedValue);
}
