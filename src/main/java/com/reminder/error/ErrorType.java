package com.reminder.error;

import org.springframework.http.HttpStatus;

public enum ErrorType {

    CONFLICT("Запрос конфликтует с состоянием системы", HttpStatus.CONFLICT),
    NOT_FOUND("Не найдено", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("Внутренняя ошибка", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("Ошибка валидации", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String title;
    private final HttpStatus httpStatus;

    ErrorType(String title, HttpStatus httpStatus) {
        this.title = title;
        this.httpStatus = httpStatus;
    }
}
