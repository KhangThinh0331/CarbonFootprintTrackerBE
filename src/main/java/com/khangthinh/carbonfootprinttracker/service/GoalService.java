package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.GoalResponse;
import com.khangthinh.carbonfootprinttracker.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoalService {
    Page<GoalResponse> getUserGoals(String username, Goal.GoalStatus status, Pageable pageable);

    Goal createGoal(String username, Goal goalRequest);

    GoalResponse updateGoalProgress(Long goalId, Double addedValue);
}
