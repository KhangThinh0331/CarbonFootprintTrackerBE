package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.dto.GoalResponse;
import com.khangthinh.carbonfootprinttracker.entity.Goal;
import com.khangthinh.carbonfootprinttracker.service.GoalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Validated
public class GoalController {

    private final GoalService goalService;

    // Lấy danh sách mục tiêu của người dùng hiện tại
    @GetMapping
    public ResponseEntity<?> getMyGoals(Principal principal, @RequestParam(required = false) Goal.GoalStatus status, @PageableDefault(size = 5, page = 0) Pageable pageable) {
        return ResponseEntity.ok(goalService.getUserGoals(principal.getName(), status, pageable));
    }

    // Tạo mục tiêu mới
    @PostMapping
    public ResponseEntity<?> createGoal(@Valid @RequestBody Goal goalRequest, Principal principal) {
            Goal newGoal = goalService.createGoal(principal.getName(), goalRequest);
            return ResponseEntity.ok(newGoal);
    }

    // Cập nhật tiến độ mục tiêu (cộng thêm giá trị)
    @PutMapping("/{goalId}/progress")
    public ResponseEntity<?> updateProgress(
            @PathVariable Long goalId,
            @RequestParam @Positive(message = "Giá trị cộng thêm phải lớn hơn 0") Double addedValue) {
            GoalResponse updatedGoal = goalService.updateGoalProgress(goalId, addedValue);
            return ResponseEntity.ok(updatedGoal);
    }
}