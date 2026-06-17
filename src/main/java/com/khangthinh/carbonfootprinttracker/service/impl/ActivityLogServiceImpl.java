package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.dto.ActivityLogResponse;
import com.khangthinh.carbonfootprinttracker.dto.ChartDataResponse;
import com.khangthinh.carbonfootprinttracker.entity.ActivityLog;
import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.mapper.ActivityLogMapper;
import com.khangthinh.carbonfootprinttracker.repository.ActivityLogRepository;
import com.khangthinh.carbonfootprinttracker.repository.DailyCo2Projection;
import com.khangthinh.carbonfootprinttracker.repository.EmissionFactorRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import com.khangthinh.carbonfootprinttracker.service.ActivityLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {
    private final ActivityLogRepository activityLogRepository;
    private final EmissionFactorRepository emissionFactorRepository;
    private final UserRepository userRepository;
    private final ActivityLogMapper activityLogMapper;

    @Transactional
    @Override
    public ActivityLog logActivity(String username, Long factorId, Double quantity, String note) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        EmissionFactor factor = emissionFactorRepository.findById(factorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hệ số phát thải"));

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

    @Override
    public Page<ActivityLogResponse> getUserLogs(String username, Integer month, Integer year, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Page<ActivityLog> logs = activityLogRepository.findByUserAndMonthAndYear(user, month, year, pageable);

        return logs.map(activityLogMapper::toResponseDto);
    }

    @Override
    public Double getTotalCo2(String username, Integer month, Integer year) {
        Double total = activityLogRepository.sumCo2ByUsername(username, month, year);
        return total != null ? total : 0.0;
    }

    @Override
    public List<ChartDataResponse> getChartDataLast7Days(String username) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0);
        List<DailyCo2Projection> dbData = activityLogRepository.sumCo2ByDateForLast7Days(username, startDate);

        Map<LocalDate, Double> dataMap = dbData.stream()
                .collect(Collectors.toMap(
                        proj -> proj.getLogDate().toLocalDate(),
                        DailyCo2Projection::getTotalCo2
                ));

        List<ChartDataResponse> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Double co2 = dataMap.getOrDefault(date, 0.0);

            result.add(new ChartDataResponse(date.format(formatter), co2));
        }

        return result;
    }
}
