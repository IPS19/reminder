package com.reminder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Data
@Builder
public class ErrorResponse {

    private int statusCode;
    private String errorCode;
    private String message;
    private LocalDateTime errorDateTime;
}
