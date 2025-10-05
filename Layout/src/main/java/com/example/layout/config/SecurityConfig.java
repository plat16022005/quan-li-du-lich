package com.example.layout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ❌ Tắt CSRF để tránh lỗi 403 khi submit form (sau này có thể bật lại)
            .csrf(csrf -> csrf.disable())

            // ✅ Cho phép mọi request (chưa khóa chặt bảo mật)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll() // cho phép truy cập form login
                .anyRequest().permitAll()
            )

            // ✅ Dùng trang login của bạn thay vì login mặc định
            .formLogin(form -> form
                .loginPage("/login")           // dùng login.html của bạn
                .loginProcessingUrl("/login")  // nơi form gửi POST (phải trùng form action)
                .permitAll()
            )

            // ✅ Cho phép logout bình thường
            .logout(logout -> logout.permitAll());

        return http.build();
    }
}
