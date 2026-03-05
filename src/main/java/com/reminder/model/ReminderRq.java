package com.reminder.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderRq {

    @NotBlank
    @Min(4)
    @Max(128)
    private String title;

    @NotBlank
    @Min(4)
    private String description;

    @NotNull
    private LocalDateTime remind;
}
