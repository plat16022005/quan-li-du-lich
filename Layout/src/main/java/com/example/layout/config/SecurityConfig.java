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
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
            .anyRequest().permitAll()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login") // ✅ đổi chỗ này
            .permitAll()
        );

        // Allow pages to be displayed in an iframe from the same origin (used by nhanvien manager-tour iframe)
        http.headers(headers -> headers.frameOptions().sameOrigin());
        http.exceptionHandling(e -> e
        	    .accessDeniedPage("/access_denied")
        	);
        return http.build();
    }
}
