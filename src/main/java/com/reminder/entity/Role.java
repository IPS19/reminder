package com.reminder.entity;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public @Nullable String getAuthority() {
        //   https://stackoverflow.com/a/19542316/548473
        return "ROLE_" + name();
    }
}
