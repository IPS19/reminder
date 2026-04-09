package com.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reminder.config.TestSecurityConfig;
import com.reminder.entity.Reminder;
import com.reminder.entity.User;
import com.reminder.model.ReminderRq;
import com.reminder.repository.ReminderJpaRepository;
import com.reminder.repository.UserJpaRepository;
import com.reminder.service.AuthService;
import com.reminder.service.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.reminder.util.ConstantUtil.DEFAULT_PAGE_SIZE;
import static com.reminder.util.ConstantUtil.DEFAULT_SORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ReminderControllerTestContainersTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("reminder_test")
            .withUsername("test_user")
            .withPassword("test_pass");
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ReminderService service;
    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ReminderJpaRepository reminderRepository;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    private ObjectMapper objectMapper;

    public static final LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2001, 1, 1, 12, 0);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Отключаем Liquibase/Flyway для простого теста, если нужно
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createNew() throws Exception {
        User user = User.builder()
                .userName("user-name")
                .email("user-email@email.com")
                .googleSubNumber("123445")
                .build();
        User savedUser = userRepository.save(user);

        ReminderRq reminderRq = ReminderRq.builder()
                .remind(TEST_DATE_TIME)
                .description("")
                .title("")
                .build();

        when(authService.getCurrentUserId()).thenReturn(savedUser.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(ReminderController.REST_URL)
                        .with(jwt().jwt(jwt -> jwt.subject(savedUser.getId().toString())
                                .claim("email", savedUser.getEmail())
                                .claim("sub", savedUser.getGoogleSubNumber())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reminderRq)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Page<Reminder> reminders = reminderRepository.getList(savedUser.getId(), PageRequest.of(1, DEFAULT_PAGE_SIZE, DEFAULT_SORT));
        assertFalse(reminders.isEmpty());
    }

}