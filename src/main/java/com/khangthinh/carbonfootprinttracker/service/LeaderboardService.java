package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.LeaderboardResponse;

import java.util.List;

public interface LeaderboardService {
    List<LeaderboardResponse> getCurrentMonthLeaderboard();
}
