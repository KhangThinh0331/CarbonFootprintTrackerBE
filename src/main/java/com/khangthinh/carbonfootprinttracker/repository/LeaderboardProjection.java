package com.khangthinh.carbonfootprinttracker.repository;

public interface LeaderboardProjection {
    Integer getRealRank();
    String getFullName();
    String getAvatarUrl();
    Double getTotalCo2();
}
