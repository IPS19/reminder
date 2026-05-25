package com.reminder.error;

public class ReminderNotFoundException extends BaseReminderException {
    public ReminderNotFoundException(Long id) {
        super("Напоминание с id" + id + "не найдено", ErrorType.NOT_FOUND);
    }
}
