package com.khangthinh.carbonfootprinttracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khangthinh.carbonfootprinttracker.service.impl.UserDetailsServiceImpl;
import com.khangthinh.carbonfootprinttracker.util.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Bật CORS cho Next.js
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF vì dùng JWT (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        // Trả về 401 Unauthorized khi Token sai/thiếu
                        .authenticationEntryPoint((request, response, authException) -> {
                            Map<String, Object> data = new HashMap<>();
                            data.put("error", "Unauthorized");
                            data.put("message", "Vui lòng đăng nhập để thực hiện hành động này");

                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                            // Dùng ObjectMapper để convert Map sang JSON string
                            new ObjectMapper().writeValue(response.getOutputStream(), data);
                        })
                        // Trả về 403 Forbidden khi sai quyền (Role)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            Map<String, Object> data = new HashMap<>();
                            data.put("error", "Forbidden");
                            data.put("message", "Bạn không có quyền thực hiện hành động này");

                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                            // Dùng ObjectMapper để convert Map sang JSON string
                            new ObjectMapper().writeValue(response.getOutputStream(), data);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/leaderboard/monthly").permitAll() // Cho phép đăng nhập/đăng ký không cần token
                        .anyRequest().authenticated() // Các API còn lại bắt buộc phải có Token hợp lệ
                );

        http.authenticationProvider(authenticationProvider());

        // Chèn JwtFilter vào trước Filter mặc định của Spring Security
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Cấu hình CORS để Next.js (cổng 3000) có thể gọi API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Tên miền của Next.js
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}