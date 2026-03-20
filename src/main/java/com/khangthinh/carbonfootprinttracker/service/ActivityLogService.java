package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.ActivityLogResponse;
import com.khangthinh.carbonfootprinttracker.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityLogService {
    ActivityLog logActivity(String username, Long factorId, Double quantity, String note);

    Page<ActivityLogResponse> getUserLogs(String username, Pageable pageable);
}
