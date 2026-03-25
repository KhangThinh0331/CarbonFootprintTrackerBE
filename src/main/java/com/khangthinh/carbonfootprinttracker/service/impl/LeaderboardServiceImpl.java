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

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private final ActivityLogRepository activityLogRepository;

    @Override
    public List<LeaderboardResponse> getCurrentMonthLeaderboard() {
        // 1. Xác định ngày đầu tháng và cuối tháng hiện tại
        LocalDate now = LocalDate.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);

        // 2. Kéo dữ liệu từ DB (Đã được sắp xếp ASC)
        List<LeaderboardProjection> rawData = activityLogRepository.getMonthlyLeaderboard(startOfMonth, endOfMonth, PageRequest.of(0, 100));

        // 3. Gán Rank và Badge
        List<LeaderboardResponse> leaderboard = new ArrayList<>();
        int rank = 1;

        for (LeaderboardProjection row : rawData) {
            String badge = "";
            if (rank == 1) badge = "Người hùng Trái Đất";
            else if (rank == 2) badge = "Hiệp sĩ Xanh";
            else if (rank == 3) badge = "Mầm non Hy vọng";
            else badge = "Cư dân Tích cực";

            leaderboard.add(LeaderboardResponse.builder()
                    .rank(rank)
                    .username(row.getUsername())
                    .avatarUrl(row.getAvatarUrl())
                    .totalCo2(row.getTotalCo2())
                    .badge(badge)
                    .build());
            rank++;
        }

        return leaderboard;
    }
}
