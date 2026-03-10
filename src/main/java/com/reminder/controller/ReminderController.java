package com.reminder.controller;

import com.reminder.entity.Reminder;
import com.reminder.model.ReminderRq;
import com.reminder.service.ReminderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.reminder.controller.ReminderController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ReminderController {

    static final String REST_URL = "/api/v1";

    public ReminderController(ReminderService service) {
        this.service = service;
    }

    private final ReminderService service;

    @PostMapping
    public ResponseEntity<Reminder> create(@Valid @RequestBody ReminderRq request) {
        Reminder newReminder = service.saveNew(request);
        log.info("Добавлено новое напоминание");

        return ResponseEntity.status(HttpStatus.CREATED).body(newReminder);
    }

    @PutMapping("/{id}")
    public void update(@Valid @RequestBody ReminderRq request, @PathVariable Long id) {
        log.info("Изменено напоминание с id: {}", id);
        service.update(request, id);
    }

    @GetMapping("/sort")
    public Page<Reminder> getSorted(
            @RequestParam(required = false) String dateTime,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer currentPage) {
        return service.getSorted(dateTime, name, currentPage);
    }

    @GetMapping("/filter")
    public Page<Reminder> getFiltered(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "hh:mm") LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "hh:mm") LocalTime endTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "hh:mm") Integer currentPage) {
        log.info("запрос на список уведомлений по фильтру");
        return service.getFiltered(startDate, startTime, endDate, endTime, currentPage);
    }

    @GetMapping("/list")
    public Page<Reminder> getList(@RequestParam(required = false) Integer total,
                                  @RequestParam(required = false) Integer current) {
        log.info("запрос на список всех уведомлений");
        return service.getList(current, total);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("заспрос на удаление напоминания с id {}", id);
        service.delete(id);
    }
}
