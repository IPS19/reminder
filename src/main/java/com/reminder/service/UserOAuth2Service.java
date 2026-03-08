package com.reminder.service;

import com.reminder.entity.User;
import com.reminder.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.reminder.util.ConstantUtil.EMAIL;
import static com.reminder.util.ConstantUtil.GOOGLE_SUB_NUMBER;
import static com.reminder.util.ConstantUtil.ID;
import static com.reminder.util.ConstantUtil.TELEGRAM_CHAT_ID;
import static com.reminder.util.ConstantUtil.USER_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOAuth2Service extends DefaultOAuth2UserService {

    private final UserJpaRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Этот метод вызывается Spring Security АВТОМАТИЧЕСКИ
        // при входе через Google
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute(EMAIL);
        String name = oAuth2User.getAttribute("name");
        String sub = oAuth2User.getAttribute("sub"); // Google отправляет sub как уникальный ID

        User user = userRepository.findUserBySubNumber(sub)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .userName(name == null ? email : name)
                            .googleSubNumber(sub)
                            .build();
                    log.info("Создание новго пользователя: {}", email);
                    return userRepository.save(newUser);
                });

        Map<String, Object> userAttributes = Map.of(
                ID, user.getId(),
                EMAIL, user.getEmail(),
                USER_NAME, user.getUserName(),
                GOOGLE_SUB_NUMBER, user.getGoogleSubNumber(),
                TELEGRAM_CHAT_ID,user.getTelegramChatId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                userAttributes,
                "email"  // атрибут для name
        );
    }
}
