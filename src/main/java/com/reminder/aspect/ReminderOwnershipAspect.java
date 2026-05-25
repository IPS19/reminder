package com.reminder.aspect;

import com.reminder.annotation.CheckUserOwnsReminder;
import com.reminder.entity.Reminder;
import com.reminder.error.ReminderNotFoundException;
import com.reminder.error.ReminderOwnerConflictException;
import com.reminder.model.AuthUser;
import com.reminder.repository.ReminderJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ReminderOwnershipAspect {

    private final ReminderJpaRepository reminderRepository;

    @Before("@annotation(checkUserOwnsReminder)")
    public void checkOwnership(JoinPoint joinPoint, CheckUserOwnsReminder checkUserOwnsReminder) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = signature.getMethod().getParameters();

        // Получаем значение reminderId из параметра метода
        Long reminderId = null;
        String paramName = checkUserOwnsReminder.reminderIdParam();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                reminderId = (Long) args[i];
                break;
            }
        }

        if (reminderId == null) {
            throw new IllegalArgumentException("Не найден параметр метода с именем: " + paramName);
        }

        // Проверка принадлежности напоминания пользователю
        Optional<Reminder> reminder = reminderRepository.findByIdWithUser(reminderId);

        Long authUserId = AuthUser.get().id();
        if (reminder.isEmpty()) {
            throw new ReminderNotFoundException(reminderId);
        }

        if (!Objects.equals(reminder.get().getUser().getId(), authUserId)) {
            throw new ReminderOwnerConflictException(reminderId, authUserId);
        }
    }
}
