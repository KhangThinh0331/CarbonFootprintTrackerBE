package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.dto.LeaderboardResponse;
import com.khangthinh.carbonfootprinttracker.repository.ActivityLogRepository;
import com.khangthinh.carbonfootprinttracker.repository.LeaderboardProjection;
import com.khangthinh.carbonfootprinttracker.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private final ActivityLogRepository activityLogRepository;

    @Override
    public List<LeaderboardResponse> getCurrentMonthLeaderboard(String fullName) {
        // 1. Xác định ngày đầu tháng và cuối tháng hiện tại
        LocalDate now = LocalDate.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);

        String nameParam = (fullName != null && !fullName.trim().isEmpty()) ? fullName : null;

        // 2. Kéo dữ liệu từ DB (Đã được sắp xếp ASC)
        List<LeaderboardProjection> rawData = activityLogRepository.getMonthlyLeaderboard(startOfMonth, endOfMonth, nameParam, PageRequest.of(0, 100));

        // 3. Gán Rank và Badge
        return rawData.stream().map(row -> {
            int rank = row.getRealRank();

            String badge = (rank == 1) ? "Người hùng Trái Đất" :
                    (rank == 2) ? "Hiệp sĩ Xanh" :
                            (rank == 3) ? "Mầm non Hy vọng" : "Cư dân Tích cực";

            return LeaderboardResponse.builder()
                    .rank(rank)
                    .fullName(row.getFullName())
                    .avatarUrl(row.getAvatarUrl())
                    .totalCo2(row.getTotalCo2())
                    .badge(badge)
                    .build();
        }).collect(Collectors.toList());
    }
}
