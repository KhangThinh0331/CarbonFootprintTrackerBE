package com.khangthinh.carbonfootprinttracker.util;

import com.khangthinh.carbonfootprinttracker.entity.OtpToken;
import com.khangthinh.carbonfootprinttracker.repository.OtpTokenRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanupTask {

    private final UserRepository userRepository;

    private final OtpTokenRepository otpTokenRepository;

    // Chạy vào 2 giờ sáng mỗi ngày (Cron expression: Giây Phút Giờ Ngày Tháng Thứ)
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void deleteUnverifiedUsers() {
        // Xác định mốc thời gian: Những user tạo cách đây hơn 24h
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);

        // 1. Tìm danh sách User chưa kích hoạt và quá hạn
        // Giả sử bạn có trường 'createdAt' trong entity User
        userRepository.deleteByIsActiveFalseAndCreatedAtBefore(threshold);
        otpTokenRepository.deleteByTypeAndLastSentAtBefore(OtpToken.OtpType.FORGOT_PASSWORD, threshold);

        System.out.println("Đã dọn dẹp các tài khoản và mã otp rác lúc: " + LocalDateTime.now());
    }
}
