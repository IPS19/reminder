package com.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reminder.repository.ReminderJpaRepository;
import com.reminder.repository.UserJpaRepository;
import com.reminder.service.AuthService;
import com.reminder.service.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.time.LocalDateTime;

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

    private ObjectMapper objectMapper;

    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2001, 1, 1, 12, 0);


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

    }

}