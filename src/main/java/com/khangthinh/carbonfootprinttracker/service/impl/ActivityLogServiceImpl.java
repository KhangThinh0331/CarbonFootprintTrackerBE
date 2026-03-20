package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.dto.ActivityLogResponse;
import com.khangthinh.carbonfootprinttracker.entity.ActivityLog;
import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.mapper.ActivityLogMapper;
import com.khangthinh.carbonfootprinttracker.repository.ActivityLogRepository;
import com.khangthinh.carbonfootprinttracker.repository.EmissionFactorRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import com.khangthinh.carbonfootprinttracker.service.ActivityLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {
    private final ActivityLogRepository activityLogRepository;
    private final EmissionFactorRepository emissionFactorRepository;
    private final UserRepository userRepository;
    private final ActivityLogMapper activityLogMapper;

    // 1. Ghi nhận hoạt động mới và tính toán CO2
    @Transactional
    public ActivityLog logActivity(String username, Long factorId, Double quantity, String note) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        EmissionFactor factor = emissionFactorRepository.findById(factorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hệ số phát thải"));

        // Logic cốt lõi: Tính toán CO2
        Double calculatedCo2 = quantity * factor.getCo2PerUnit();

        ActivityLog log = ActivityLog.builder()
                .user(user)
                .emissionFactor(factor)
                .quantity(quantity)
                .totalCo2(calculatedCo2)
                .note(note)
                .build();

        return activityLogRepository.save(log);
    }

    // 2. Lấy lịch sử của user (Dùng cho Next.js hiển thị Dashboard)
    @Override
    public Page<ActivityLogResponse> getUserLogs(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Page<ActivityLog> logs = activityLogRepository.findByUser(user, pageable);

        // Map list Entity sang list DTO tự động
        return logs.map(activityLogMapper::toResponseDto);
    }
}
