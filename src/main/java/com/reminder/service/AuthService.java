package com.reminder.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final int MOCK_AUTH_USER_ID = 1;

    public int getUserId() {
        return MOCK_AUTH_USER_ID;
    }
}
