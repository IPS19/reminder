package com.reminder.service;

import com.reminder.model.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
public class AuthService {
    public static AuthUser safeGet() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        return (auth.getPrincipal() instanceof AuthUser au) ? au : null;
    }

    public static AuthUser get() {
        return requireNonNull(safeGet(), "No authorized user found");
    }

}
