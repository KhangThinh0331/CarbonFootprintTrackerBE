package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.ActivityLogResponse;
import com.khangthinh.carbonfootprinttracker.dto.ChartDataResponse;
import com.khangthinh.carbonfootprinttracker.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityLogService {
    ActivityLog logActivity(String username, Long factorId, Double quantity, String note);

    Page<ActivityLogResponse> getUserLogs(String username, Integer month, Integer year, Pageable pageable);

    Double getTotalCo2(String username, Integer month, Integer year);

    List<ChartDataResponse> getChartDataLast7Days(String username);
}
