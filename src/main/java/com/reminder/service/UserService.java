package com.reminder.service;

import com.reminder.entity.Role;
import com.reminder.entity.User;
import com.reminder.model.RegisterRq;
import com.reminder.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRq request) {

        Optional<User> checkExistUser = userRepository.findByEmailIgnoreCase(request.getEmail());
        if (checkExistUser.isPresent()) {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }

        log.info("Регистрация нового пользователя {}", request.getEmail());
        return userRepository.save(
                User.builder()
                        .email(request.getEmail().toLowerCase())
                        .name(request.getName())
                        .password(passwordEncoder.encode(request.getPassword()))// 🔐 Шифруем пароль перед сохранением
                        .roles(Set.of(Role.USER))
                        .build()
        );
    }
}
