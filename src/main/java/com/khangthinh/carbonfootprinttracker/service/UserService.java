package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.ChangePasswordRequest;
import com.khangthinh.carbonfootprinttracker.entity.User;

public interface UserService {
    User getUserProfile(String username);

    User updateCarbonTarget(String username, Double newTarget);

    String changePassword(String username, ChangePasswordRequest request);
}
