package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác minh tài khoản - Carbon Footprint Tracker");

            // Tạo nội dung HTML cho email nhìn xịn xò hơn
            String htmlContent = "<h3>Chào mừng bạn đến với Carbon Footprint Tracker!</h3>"
                    + "<p>Mã OTP xác minh tài khoản của bạn là: <b style='font-size: 20px; color: green;'>" + otpCode + "</b></p>"
                    + "<p>Mã này sẽ hết hạn trong 5 phút.</p>"
                    + "<p>Vui lòng không chia sẻ mã này cho bất kỳ ai.</p>";

            helper.setText(htmlContent, true); // Set 'true' để báo cho Spring biết đây là HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
