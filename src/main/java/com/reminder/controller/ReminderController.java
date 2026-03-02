package com.reminder.controller;

import com.reminder.entity.Reminder;
import com.reminder.model.ReminderRq;
import com.reminder.service.ReminderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.reminder.controller.ReminderController.REST_URL;

@RestController
@RequestMapping(REST_URL)
@Slf4j
public class ReminderController {

    static final String REST_URL = "/api/v1";

    private ReminderService service;

    @PostMapping
    public ResponseEntity<Reminder> create(@Valid @RequestBody ReminderRq request) {
        Reminder newReminder = service.saveNew(request);
        log.info("Добавлено новое напоминание");
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(newReminder.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(newReminder);
    }

    @PutMapping
    public void update(@Valid @RequestBody ReminderRq request, @PathVariable int id) {
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
    public void delete(@PathVariable int id) {
        service.delete(id);
        log.info("удалено напоминание с id {}", id);
    }
}
