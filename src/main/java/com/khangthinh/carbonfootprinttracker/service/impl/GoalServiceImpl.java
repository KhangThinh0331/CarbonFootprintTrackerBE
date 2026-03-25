package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.entity.Goal;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.repository.GoalRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import com.khangthinh.carbonfootprinttracker.service.GoalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Goal> getUserGoals(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return goalRepository.findByUser(user, pageable);
    }

    @Transactional
    @Override
    public Goal createGoal(String username, Goal goalRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        goalRequest.setUser(user);
        goalRequest.setCurrentValue(0.0); // Khởi tạo tiến độ bằng 0
        return goalRepository.save(goalRequest);
    }

    @Transactional
    @Override
    public Goal updateGoalProgress(Long goalId, Double addedValue) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục tiêu"));

        goal.setCurrentValue(goal.getCurrentValue() + addedValue);
        return goalRepository.save(goal);
    }
}
