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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        List<CompletableFuture<Void>> mailSendFutures = new ArrayList<>();
        remindersForSend.forEach(reminder -> {
            CompletableFuture<Void> mailSendFuture = CompletableFuture.runAsync(() -> {
                emailService.sendSimpleMessage(reminder.getUser().getEmail(),
                        reminder.getTitle(),
                        reminder.getDescription());

            }).exceptionally(throwable -> {
                log.error("Ошибка отправки на напоминания на почту: {}, {}",
                        reminder.getUser().getEmail(),
                        throwable.getMessage());
                return null;
            });

            mailSendFutures.add(mailSendFuture);
        });
        CompletableFuture.allOf(mailSendFutures.toArray(new CompletableFuture[0])).join();

        List<CompletableFuture<Void>> telegramSendFutures = new ArrayList<>();
        remindersForSend.stream()
                .filter(reminder -> reminder.getUser().getTelegramChatId() != null)
                .forEach(reminder -> {
                    CompletableFuture<Void> mailSendFuture = CompletableFuture.runAsync(() -> {
                        reminderBot.sendMessage(reminder.getUser().getTelegramChatId(),
                                String.format("%s, описание: %s", reminder.getTitle(), reminder.getDescription()));

                    }).exceptionally(throwable -> {
                        log.error("Ошибка отправки на напоминания в телеграмм пользователю с id {}, {}",
                                reminder.getUser().getId(),
                                throwable.getMessage());
                        return null;
                    });

                    telegramSendFutures.add(mailSendFuture);
                });
        CompletableFuture.allOf(telegramSendFutures.toArray(new CompletableFuture[0])).join();


    }
}
