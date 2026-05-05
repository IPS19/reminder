package com.reminder.configuration;

import com.reminder.entity.User;
import com.reminder.model.AuthUser;
import com.reminder.repository.UserJpaRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final UserJpaRepository userRepository;

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Для REST API отключаем CSRF. CSRF нужен для веб-форм (чтобы злой сайт не отправлял запросы от имени пользователя). Для REST API (Postman, мобильные приложения) он не нужен и только мешает.
                .sessionManagement(session -> session //  как Spring будет создавать и хранить сессии
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //IF_REQUIRED - создавать сессию только если она нужна (при логине)
                        .sessionFixation().migrateSession() //Защита от фиксации сессии (session fixation attack). При логине создается НОВАЯ сессия, а старая (анонимная) копируется и удаляется. Хакер не сможет подсунуть пользователю свою сессию.
                        .maximumSessions(1) //Если пользователь залогинится на другом устройстве - старая сессия будет вытеснена.
                        .maxSessionsPreventsLogin(false) // Не блокируем новый логин, а вытесняем старый - false - новый логин разрешен, старый вытесняется. true - новый логин будет отклонен, пока старый не завершится.
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**",
                                "/error",
                                "/actuator/prometheus",
                                "/api/auth/login",
                                "/profiles").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login") //логин через форму/JSON, а не Basic Auth
                        .successHandler(authenticationSuccessHandler()) //Вызывается, когда пароль верный. Здесь мы возвращаем JSON: {"status":"success"} вместо редиректа на страницу
                        .failureHandler(authenticationFailureHandler()) // Вызывается при неверном пароле или если пользователь не найден. Возвращает JSON с ошибкой.
                        .usernameParameter("email")  // 👈 явно говорим, что поле называется "email"
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler()) //Кастомный обработчик успешного выхода. Возвращает JSON: {"status":"logged out"}.
                        .invalidateHttpSession(true) //Уничтожает HTTP-сессию на сервере. Данные сессии (корзина, авторизация и т.д.) удаляются.
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {// Вызывается, когда пользователь пытается получить доступ к защищенному ресурсу без аутентификации
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //Устанавливает HTTP статус 401 (Unauthorized).
                            response.setContentType("application/json"); //
                            response.getWriter().write("{\"error\":\"Unauthorized\", \"message\":\"Please login first\"}"); //Отправляет JSON-сообщение с ошибкой, вместо стандартной страницы логина.
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Login successful\"}");
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Authentication failed\", \"message\":\"" + exception.getMessage() + "\"}");
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) throws IOException {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Logged out successfully\"}");
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                return new AuthUser(user);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());

        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // Обязательно для Spring Security!
        return new JdkSerializationRedisSerializer();
    }
}
