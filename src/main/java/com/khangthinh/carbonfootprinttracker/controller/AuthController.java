package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.dto.*;
import com.khangthinh.carbonfootprinttracker.service.AuthService;
import com.khangthinh.carbonfootprinttracker.util.TokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
            String result = authService.registerUser(request);
            return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenService.generateJwtToken(authentication);

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setUsername(loginRequest.getUsername());
        response.setMessage("Đăng nhập thành công!");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam @NotBlank(message = "Mã số OTP không được để trống") String otp) {
            String result = authService.verifyEmail(otp);
            return ResponseEntity.ok(result);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam @NotBlank(message = "Email không được để trống") @Email(message = "Email không đúng định dạng") String email) {
        String result = authService.resendOtp(email);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam @NotBlank(message = "Email không được để trống") @Email(message = "Email không đúng định dạng") String email) {
        String result = authService.forgotPassword(email);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String result = authService.resetPassword(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/resend-forgot-password-otp")
    public ResponseEntity<?> resendForgotPasswordOtp(@RequestParam @NotBlank(message = "Email không được để trống") @Email(message = "Email không đúng định dạng") String email) {
        String result = authService.resendForgotPasswordOtp(email);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
            AuthResponse response = authService.loginWithGoogle(request.getIdToken());
            return ResponseEntity.ok(response);
    }
}
