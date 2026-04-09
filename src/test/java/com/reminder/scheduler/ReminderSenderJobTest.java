package com.reminder.scheduler;

import com.reminder.entity.Reminder;
import com.reminder.repository.ReminderJpaRepository;
import com.reminder.service.EmailSendService;
import com.reminder.telegram.ReminderBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import java.util.List;

import static com.reminder.util.TestUtil.REMINDER1;
import static com.reminder.util.TestUtil.REMINDER2;
import static com.reminder.util.TestUtil.REMINDER3;
import static com.reminder.util.TestUtil.TEST_REMINDERS;
import static com.reminder.util.TestUtil.USER1;
import static com.reminder.util.TestUtil.USER2;
import static com.reminder.util.TestUtil.USER3_NO_TELEGRAM;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderSenderJobTest {

    @InjectMocks
    private ReminderSenderJob job;

    @Mock
    private ReminderBot reminderBot;

    @Mock
    private EmailSendService emailService;

    @Mock
    private ReminderJpaRepository repository;

    @Mock
    private JobExecutionContext executionContext;

    List<Reminder> testReminders;

    @BeforeEach
    void setUp() {
        testReminders = TEST_REMINDERS;
    }

    @Test
    void executeInternal() {
        when(repository.getRemindersForSend(any(), any())).thenReturn(testReminders);

        job.executeInternal(executionContext);

        verify(emailService).sendSimpleMessage(eq(USER2.getEmail()), eq(REMINDER2.getTitle()), eq(REMINDER2.getDescription()));
        verify(emailService).sendSimpleMessage(eq(USER1.getEmail()), eq(REMINDER1.getTitle()), eq(REMINDER1.getDescription()));
        verify(emailService).sendSimpleMessage(eq(USER3_NO_TELEGRAM.getEmail()), eq(REMINDER3.getTitle()), eq(REMINDER3.getDescription()));

        verify(reminderBot).sendMessage(eq(USER1.getTelegramChatId()), eq(REMINDER1.getTitle() + ", описание: " + REMINDER1.getDescription()));
        verify(reminderBot).sendMessage(eq(USER2.getTelegramChatId()), eq(REMINDER2.getTitle() + ", описание: " + REMINDER2.getDescription()));
    }

    @Test
    void executeInternal_EmptyRemindersList() {
        when(repository.getRemindersForSend(any(), any())).thenReturn(emptyList());

        job.executeInternal(executionContext);

        verify(emailService, never()).sendSimpleMessage(any(), any(), any());
        verify(reminderBot, never()).sendMessage(any(), any());
    }
}