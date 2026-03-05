package com.reminder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // разрешить все запросы
                )
                .csrf(csrf -> csrf.disable()); // отключить CSRF для тестов

        return http.build();
    }
}
