package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.dto.GoalResponse;
import com.khangthinh.carbonfootprinttracker.entity.Goal;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.mapper.GoalMapper;
import com.khangthinh.carbonfootprinttracker.repository.GoalRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import com.khangthinh.carbonfootprinttracker.service.GoalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;

    @Override
    public Page<GoalResponse> getUserGoals(String username, Goal.GoalStatus status, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Page<Goal> goals = goalRepository.findByUserAndStatusOptional(user, status, pageable);
        return goals.map(goalMapper::toResponseDto);
    }

    @Transactional
    @Override
    public Goal createGoal(String username, Goal goalRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        goalRequest.setUser(user);
        goalRequest.setCurrentValue(0.0);
        goalRequest.setStatus(Goal.GoalStatus.IN_PROGRESS);// Khởi tạo tiến độ bằng 0
        return goalRepository.save(goalRequest);
    }

    @Transactional
    @Override
    public GoalResponse updateGoalProgress(Long goalId, Double addedValue) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục tiêu"));

        goal.setCurrentValue(goal.getCurrentValue() + addedValue);
        updateSingleGoalStatus(goal);
        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toResponseDto(updatedGoal);
    }

    private void updateSingleGoalStatus(Goal goal) {
        LocalDate now = LocalDate.now();

        if (goal.getCurrentValue() > goal.getTargetValue()) {
            goal.setStatus(Goal.GoalStatus.FAILED);
        }
        else if (goal.getDeadline() != null && now.isAfter(goal.getDeadline())) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        }
    }
}
