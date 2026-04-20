package com.reminder.util;

import com.reminder.entity.Reminder;
import com.reminder.entity.Role;
import com.reminder.entity.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@UtilityClass
public class TestUtil {
    public static final User USER1 = User.builder()
            .id(1L)
            .userName("user1")
            .role(Set.of(Role.USER))
            .password("password1")
            .email("user1@email.com")
            .telegramChatId(1L)
            .build();
    public static final User USER2 = User.builder()
            .id(1L)
            .userName("user2")
            .role(Set.of(Role.USER))
            .password("password2")
            .email("user2@email.com")
            .telegramChatId(2L)
            .build();

    public static final User USER3_NO_TELEGRAM = User.builder()
            .id(1L)
            .userName("user2")
            .role(Set.of(Role.USER))
            .password("password2")
            .email("user2@email.com")
            .build();

    public static final Reminder REMINDER1 = Reminder.builder()
            .id(1L)
            .title("title1")
            .description("description1")
            .remindDateTime(LocalDateTime.of(2026, 1, 1, 1, 1))
            .user(USER1)
            .build();

    public static final Reminder REMINDER2 = Reminder.builder()
            .id(2L)
            .title("title2")
            .description("description2")
            .remindDateTime(LocalDateTime.of(2026, 1, 1, 1, 1))
            .user(USER2)
            .build();

    public static final Reminder REMINDER3 = Reminder.builder()
            .id(2L)
            .title("title3")
            .description("description2")
            .remindDateTime(LocalDateTime.of(2026, 1, 1, 1, 1))
            .user(USER3_NO_TELEGRAM)
            .build();

    public static final List<Reminder> TEST_REMINDERS = List.of(REMINDER1, REMINDER2, REMINDER3);
}
