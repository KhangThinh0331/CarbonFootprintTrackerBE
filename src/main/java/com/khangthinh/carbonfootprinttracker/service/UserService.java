package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.ChangePasswordRequest;
import com.khangthinh.carbonfootprinttracker.dto.UserProfileRequest;
import com.khangthinh.carbonfootprinttracker.dto.UserProfileResponse;
import com.khangthinh.carbonfootprinttracker.entity.User;

public interface UserService {
    UserProfileResponse getUserProfile(String username);

    User updateProfile(String username, UserProfileRequest request);

    String changePassword(String username, ChangePasswordRequest request);
}
