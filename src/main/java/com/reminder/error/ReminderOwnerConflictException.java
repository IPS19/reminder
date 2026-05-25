package com.reminder.error;

public class ReminderOwnerConflictException extends BaseReminderException {
    public ReminderOwnerConflictException(Long reminderId, Long userId) {
        super("Напоминание с id : " + reminderId + " не принадлежит вользователю с id " + userId,
                ErrorType.CONFLICT);
    }
}
