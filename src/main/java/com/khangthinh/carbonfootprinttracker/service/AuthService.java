package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.AuthResponse;
import com.khangthinh.carbonfootprinttracker.dto.RegisterRequest;
import com.khangthinh.carbonfootprinttracker.dto.ResetPasswordRequest;

public interface AuthService {
    String registerUser(RegisterRequest request);

    AuthResponse loginWithGoogle(String idTokenString);

    String verifyEmail(String otpCode);

    String resendOtp(String email);

    String forgotPassword(String email);

    String resetPassword(ResetPasswordRequest request);

    String resendForgotPasswordOtp(String email);
}
