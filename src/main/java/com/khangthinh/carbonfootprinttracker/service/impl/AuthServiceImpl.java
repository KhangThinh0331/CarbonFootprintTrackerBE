package com.khangthinh.carbonfootprinttracker.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.khangthinh.carbonfootprinttracker.dto.AuthResponse;
import com.khangthinh.carbonfootprinttracker.dto.RegisterRequest;
import com.khangthinh.carbonfootprinttracker.dto.ResetPasswordRequest;
import com.khangthinh.carbonfootprinttracker.entity.OtpToken;
import com.khangthinh.carbonfootprinttracker.entity.Role;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.repository.OtpTokenRepository;
import com.khangthinh.carbonfootprinttracker.repository.RoleRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import com.khangthinh.carbonfootprinttracker.service.AuthService;
import com.khangthinh.carbonfootprinttracker.service.EmailService;
import com.khangthinh.carbonfootprinttracker.util.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(900000) + 100000; // Sinh số từ 100000 đến 999999
        return String.valueOf(num);
    }

    private void createAndSendOtp(User user, OtpToken.OtpType type) {
        otpTokenRepository.deleteByUserIdAndType(user.getId(), type);

        String otp = generateOtp();

        OtpToken otpToken = OtpToken.builder()
                .otpCode(otp)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .type(type)
                .user(user)
                .build();

        otpTokenRepository.save(otpToken);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // Nếu gửi mail lỗi (sai định dạng, lỗi server mail...),
            // ném RuntimeException để ép Spring ROLLBACK toàn bộ User và OTP vừa lưu.
            throw new RuntimeException("Không thể gửi mã xác minh. Vui lòng kiểm tra lại địa chỉ email!");
        }
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Transactional
    @Override
    public String registerUser(RegisterRequest request) {
        Optional<User> existingInactiveUser =
                userRepository.findByEmailAndIsActiveFalse(request.getEmail());

        if (existingInactiveUser.isPresent()) {
            User user = existingInactiveUser.get();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            userRepository.save(user);
            createAndSendOtp(user, OtpToken.OtpType.REGISTRATION);
            return "Email đã tồn tại nhưng chưa xác minh. OTP mới đã được gửi!";
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Lỗi: Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        // Tạo user mới
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .targetCo2Month(50.0)
                .isActive(false)
                .build();

        // Gán quyền ROLE_USER mặc định
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role."));
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        String otp = generateOtp();

        OtpToken otpToken = OtpToken.builder()
                .otpCode(otp)
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // Hết hạn sau 5 phút
                .type(OtpToken.OtpType.REGISTRATION)
                .user(user)
                .build();
        otpTokenRepository.save(otpToken);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // Nếu gửi mail lỗi (sai định dạng, lỗi server mail...),
            // ném RuntimeException để ép Spring ROLLBACK toàn bộ User và OTP vừa lưu.
            throw new RuntimeException("Không thể gửi mã xác minh. Vui lòng kiểm tra lại địa chỉ email!");
        }
        return "Đăng ký thành công! Vui lòng kiểm tra email để nhận mã xác minh (có hiệu lực trong 5 phút).";
    }

    @Transactional
    @Override
    public String verifyEmail(String otpCode) {
        // 1. Tìm OTP trong database
        OtpToken otpToken = otpTokenRepository.findByOtpCodeAndType(otpCode, OtpToken.OtpType.REGISTRATION)
                .orElseThrow(() -> new RuntimeException("Mã OTP không chính xác!"));

        // 2. Kiểm tra xem OTP đã hết hạn chưa
        if (otpToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Nếu hết hạn, xóa luôn OTP rác này và bắt người dùng đăng ký lại hoặc gửi lại mã
            otpTokenRepository.delete(otpToken);
            throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới!");
        }

        // 3. Kích hoạt User
        User user = otpToken.getUser();
        user.setActive(true);
        userRepository.save(user);

        // 4. Xóa OTP sau khi đã xác minh thành công để dọn dẹp database
        otpTokenRepository.delete(otpToken);

        return "Xác minh email thành công! Bây giờ bạn đã có thể đăng nhập.";
    }

    @Transactional
    @Override
    public String resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (user.isActive()) {
            throw new RuntimeException("Tài khoản này đã được xác minh!");
        }

        // 1. Kiểm tra xem có OTP cũ không để check thời gian gửi
        OtpToken otpToken = otpTokenRepository.findByUserIdAndType(user.getId(), OtpToken.OtpType.REGISTRATION)
                .orElseGet(() -> OtpToken.builder().user(user).type(OtpToken.OtpType.REGISTRATION).build());

        if (otpToken.getLastSentAt() != null) {
            long secondsElapsed = java.time.Duration.between(otpToken.getLastSentAt(), LocalDateTime.now()).getSeconds();
            if (secondsElapsed < 60) {
                throw new RuntimeException("Vui lòng đợi " + (60 - secondsElapsed) + " giây nữa!");
            }
        }

        String newOtp = generateOtp();
        otpToken.setOtpCode(newOtp);
        otpToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpToken.setLastSentAt(LocalDateTime.now());

        // 4. Lưu lại (Hibernate sẽ thực hiện lệnh UPDATE vì object đã có ID)
        otpTokenRepository.save(otpToken);

        try {
            emailService.sendOtpEmail(user.getEmail(), newOtp);
        } catch (Exception e) {
            // Nếu gửi mail lỗi (sai định dạng, lỗi server mail...),
            // ném RuntimeException để ép Spring ROLLBACK toàn bộ User và OTP vừa lưu.
            throw new RuntimeException("Không thể gửi mã xác minh. Vui lòng kiểm tra lại địa chỉ email!");
        }

        return "Mã xác minh mới đã được gửi!";
    }

    // API 1: Yêu cầu gửi OTP quên mật khẩu
    @Transactional
    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này!"));

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản này chưa được xác minh!");
        }
        // Xóa các mã OTP cũ (nếu có) để tránh spam hoặc trùng lặp
        otpTokenRepository.deleteByUserIdAndType(user.getId(), OtpToken.OtpType.FORGOT_PASSWORD);

        // Sinh mã OTP mới (Tái sử dụng hàm generateOtp đã viết ở bài trước)
        String otp = generateOtp();

        // Lưu OTP vào DB
        OtpToken otpToken = OtpToken.builder()
                .otpCode(otp)
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // Hết hạn sau 5 phút
                .type(OtpToken.OtpType.FORGOT_PASSWORD)
                .user(user)
                .build();
        otpTokenRepository.save(otpToken);


        try {
            // Gửi email
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // Nếu gửi mail lỗi (sai định dạng, lỗi server mail...),
            // ném RuntimeException để ép Spring ROLLBACK toàn bộ User và OTP vừa lưu.
            throw new RuntimeException("Không thể gửi mã xác minh. Vui lòng kiểm tra lại địa chỉ email!");
        }

        return "Mã xác minh đã được gửi đến email của bạn.";
    }

    // API 2: Đặt lại mật khẩu bằng OTP
    @Transactional
    @Override
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này!"));

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản này chưa được xác minh!");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ!");
        }

        OtpToken otpToken = otpTokenRepository.findByOtpCodeAndType(request.getOtpCode(), OtpToken.OtpType.FORGOT_PASSWORD)
                .orElseThrow(() -> new RuntimeException("Mã OTP không chính xác!"));

        // Kiểm tra xem OTP này có đúng là của User này không
        if (!otpToken.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Mã OTP không hợp lệ cho tài khoản này!");
        }

        // Kiểm tra hạn sử dụng
        if (otpToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            otpTokenRepository.delete(otpToken);
            throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới!");
        }

        // Đặt lại mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP sau khi sử dụng thành công
        otpTokenRepository.delete(otpToken);

        return "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập bằng mật khẩu mới.";
    }

    @Transactional
    @Override
    public String resendForgotPasswordOtp(String email) {
        // 1. Tìm User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này!"));

        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản này chưa được xác minh!");
        }
        // 2. Tìm OTP quên mật khẩu hiện tại
        OtpToken otpToken = otpTokenRepository.findByUserIdAndType(user.getId(), OtpToken.OtpType.FORGOT_PASSWORD)
                .orElseGet(() -> OtpToken.builder().user(user).type(OtpToken.OtpType.FORGOT_PASSWORD).build());

        // 3. Kiểm tra giới hạn 60 giây (Chống spam email)
        if (otpToken.getLastSentAt() != null) {
            long secondsElapsed = java.time.Duration.between(otpToken.getLastSentAt(), LocalDateTime.now()).getSeconds();
            if (secondsElapsed < 60) {
                throw new RuntimeException("Vui lòng đợi " + (60 - secondsElapsed) + " giây nữa để gửi lại mã!");
            }
        }

        // 4. Cập nhật mã mới và hạn dùng mới
        String newOtp = generateOtp();
        otpToken.setOtpCode(newOtp);
        otpToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpToken.setLastSentAt(LocalDateTime.now());

        otpTokenRepository.save(otpToken);

        // 5. Gửi lại Email
        try {
            emailService.sendOtpEmail(user.getEmail(), newOtp);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi mã xác minh. Vui lòng kiểm tra lại địa chỉ email!");
        }

        return "Mã đặt lại mật khẩu mới đã được gửi!";
    }


    @Override
    public AuthResponse loginWithGoogle(String idTokenString) {
        try {
            // 1. Khởi tạo công cụ xác minh của Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 2. Xác minh Token
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Token Google không hợp lệ!");
            }

            // 3. Lấy thông tin từ Google
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            // 4. Kiểm tra User trong Database
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // Nếu User chưa tồn tại -> Tự động Đăng ký (Auto-Provisioning)
                Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role."));

                user = User.builder()
                        .username(email) // Dùng email làm username luôn cho tiện
                        .email(email)
                        .fullName(name)
                        .avatarUrl(pictureUrl)
                        // Tạo một mật khẩu ngẫu nhiên rất dài để không ai (kể cả user) có thể dùng nó để login thường
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .targetCo2Month(50.0)
                        .isActive(true)
                        .roles(Collections.singleton(userRole))
                        .build();
                userRepository.save(user);
            }

            // 5. Tạo Authentication cho Spring Security hiểu
            List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUsername(), user.getPassword(), authorities);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 6. Cấp phát JWT của hệ thống chúng ta
            String jwt = jwtUtils.generateJwtToken(authentication);

            // TRẢ VỀ ĐỐI TƯỢNG ĐẦY ĐỦ
            AuthResponse response = new AuthResponse();
            response.setToken(jwt);
            response.setUsername(user.getUsername()); // Đã có username ở đây!
            response.setMessage("Đăng nhập Google thành công!");

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xác thực Google: " + e.getMessage());
        }
    }
}
