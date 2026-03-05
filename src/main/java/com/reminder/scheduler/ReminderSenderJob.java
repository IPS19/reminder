package com.reminder.scheduler;

import com.reminder.entity.Reminder;
import com.reminder.repository.ReminderJpaRepository;
import com.reminder.service.SendEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReminderSenderJob extends QuartzJobBean {

    private ReminderJpaRepository repository;

    private SendEmailService emailService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("Поиск и отправка уведомлений");
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> remindersForSend = repository.getRemindersForSend(now, now.plusSeconds(60));

        remindersForSend.forEach(reminder ->
                emailService.sendSimpleMessage(reminder.getUser().getUserName(),
                        reminder.getTitle(),
                        reminder.getDescription()));
    }
}
