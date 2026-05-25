package com.reminder.configuration;

import com.reminder.error.ReminderNotFoundException;
import com.reminder.error.ReminderOwnerConflictException;
import com.reminder.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReminderNotFoundException.class)
    public ErrorResponse handleReminderNotFound(ReminderNotFoundException ex) {
        return ErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .errorCode(ex.getErrorType().name())
                .errorDateTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ReminderOwnerConflictException.class)
    public ErrorResponse handleReminderOwnerConflict(ReminderOwnerConflictException ex) {
        return ErrorResponse.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .errorCode(ex.getErrorType().name())
                .errorDateTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllOtherErrors(Exception ex) {
        return ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .errorCode("UNEXPECTED_ERROR")
                .errorDateTime(LocalDateTime.now())
                .build();
    }
}
