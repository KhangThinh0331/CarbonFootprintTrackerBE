package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.dto.ActivityLogRequest;
import com.khangthinh.carbonfootprinttracker.dto.ChartDataResponse;
import com.khangthinh.carbonfootprinttracker.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    // 1. Lưu nhật ký mới
    @PostMapping
    public ResponseEntity<?> logActivity(@Valid @RequestBody ActivityLogRequest request, Principal principal) {
            // principal.getName() sẽ trả về username (hoặc email) lấy từ JWT Token
            String username = principal.getName();

            // Gọi service xử lý
            var log = activityLogService.logActivity(
                    username, request.getFactorId(), request.getQuantity(), request.getNote()
            );
            return ResponseEntity.ok(log);
    }

    // 2. Lấy lịch sử của người dùng đang đăng nhập
    @GetMapping
    public ResponseEntity<?> getMyLogs(Principal principal, @RequestParam(required = false) Integer month,
                                       @RequestParam(required = false) Integer year, @PageableDefault(size = 20, page = 0, sort = "loggedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getUserLogs(principal.getName(), month, year, pageable));
    }

    @GetMapping("/total-co2")
    public ResponseEntity<Double> getTotalCo2(Principal principal, @RequestParam(required = false) Integer month,
                                              @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(activityLogService.getTotalCo2(principal.getName(), month, year));
    }

    @GetMapping("/chart-data")
    public ResponseEntity<List<ChartDataResponse>> getChartData(Principal principal) {
        return ResponseEntity.ok(activityLogService.getChartDataLast7Days(principal.getName()));
    }
}