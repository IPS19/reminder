package com.reminder.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public OAuth2User getCurrentOAuth2User() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User)) {
            throw new RuntimeException("Текущий пользователь не является OAuth2 пользователем");
        }

        return (OAuth2User) principal;
    }

    public Long getCurrentUserId() {
        OAuth2User user = getCurrentOAuth2User();
        return user.getAttribute("id");
    }
}
