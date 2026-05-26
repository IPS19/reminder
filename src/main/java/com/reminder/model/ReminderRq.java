package com.reminder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class ReminderRq {

    @NotBlank
    @NotNull
    @Size(min = 4, max = 100)
    private String title;

    @NotBlank
    @NotNull
    @Size(min = 4, max = 200)
    private String description;

    @NotNull
    private LocalDateTime remind;
}
