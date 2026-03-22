package com.khangthinh.carbonfootprinttracker.util;

import com.khangthinh.carbonfootprinttracker.entity.Role;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.repository.RoleRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRoles_Name("ROLE_ADMIN")) {
            User user = User.builder()
                    .username("Admin")
                    .email("khangthinh0331@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Lê Khang Thịnh")
                    .targetCo2Month(50.0)
                    .isActive(true)
                    .build();
            Role userRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role."));
            user.setRoles(Collections.singleton(userRole));

            userRepository.save(user);
            System.out.println("Đã khởi tạo tài khoản Admin mặc định!");
        }
    }
}

