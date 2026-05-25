package com.reminder.error;

import lombok.Getter;

@Getter
public class BaseReminderException extends RuntimeException {
    public BaseReminderException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    private ErrorType errorType;
}
