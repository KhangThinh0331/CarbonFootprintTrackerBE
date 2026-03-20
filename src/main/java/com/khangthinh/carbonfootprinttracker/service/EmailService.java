package com.khangthinh.carbonfootprinttracker.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode);
}
