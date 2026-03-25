package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.dto.ChangePasswordRequest;
import com.khangthinh.carbonfootprinttracker.dto.UserProfileRequest;
import com.khangthinh.carbonfootprinttracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    // Lấy thông tin cá nhân (Profile)
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    // Cập nhật mục tiêu CO2 hàng tháng
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileRequest request, Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), request));
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        String result = userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(result);
    }
}