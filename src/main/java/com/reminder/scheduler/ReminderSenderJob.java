package com.reminder.scheduler;

import com.reminder.entity.Reminder;
import com.reminder.repository.ReminderJpaRepository;
import com.reminder.service.EmailSendService;
import com.reminder.telegram.ReminderBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderSenderJob extends QuartzJobBean {
    private final ReminderBot reminderBot;

    private final EmailSendService emailService;

    private final ReminderJpaRepository repository;

    @Override
    public void executeInternal(JobExecutionContext context) {
        log.info("Поиск и отправка уведомлений");
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> remindersForSend = repository.getRemindersForSend(now, now.plusSeconds(60));

        remindersForSend.forEach(reminder ->
                emailService.sendSimpleMessage(reminder.getUser().getEmail(),
                        reminder.getTitle(),
                        reminder.getDescription()));

        remindersForSend.stream()
                .filter(reminder -> reminder.getUser().getTelegramChatId() != null)
                .forEach(reminder ->
                        reminderBot.sendMessage(reminder.getUser().getTelegramChatId(),
                                String.format("%s, описание: %s", reminder.getTitle(), reminder.getDescription())));

    }
}
