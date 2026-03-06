package com.reminder.service;

import com.reminder.entity.Reminder;
import com.reminder.model.ReminderRq;
import com.reminder.repository.ReminderJpaRepository;
import com.reminder.repository.UserJpaRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static com.reminder.util.ConstantUtil.DEFAULT_PAGE_SIZE;
import static com.reminder.util.ConstantUtil.DEFAULT_SORT;
import static com.reminder.util.ConstantUtil.DEFAULT_TIME;
import static com.reminder.util.ConstantUtil.MAX_DATE;
import static com.reminder.util.ConstantUtil.MIN_DATE;
import static com.reminder.util.ConstantUtil.getDateTimeOrder;
import static com.reminder.util.ConstantUtil.getTitleOrder;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ReminderService {

    private AuthService authService;

    private ReminderJpaRepository reminderRepository;

    private UserJpaRepository userRepository;

    public Reminder saveNew(ReminderRq request) {
        Reminder reminder = mapReminder(request);

        return reminderRepository.save(reminder);
    }

    public Page<Reminder> getSorted(String dateTime, String title, Integer currentPage) {

        Sort.Order dateTimeOrder = getDateTimeOrder(dateTime);

        Sort.Order titleOrder = getTitleOrder(title);

        Sort sort = Sort.by(dateTimeOrder, titleOrder);

        Integer page = Optional.ofNullable(currentPage).orElse(1);

        Pageable pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE, sort);

        return reminderRepository.getList(authService.getUserId(), pageRequest);
    }

    public Page<Reminder> getFiltered(LocalDate startDateRq,
                                      LocalTime startTimeRq,
                                      LocalDate endDateRq,
                                      LocalTime endTimeRq,
                                      Integer currentPage
    ) {
        LocalDate startDate = Optional.ofNullable(startDateRq).orElse(MIN_DATE);
        LocalTime startTime = Optional.ofNullable(startTimeRq).orElse(DEFAULT_TIME);
        LocalDate endDate = Optional.ofNullable(endDateRq).orElse(MAX_DATE);
        LocalTime endTime = Optional.ofNullable(endTimeRq).orElse(DEFAULT_TIME);

        Integer page = Optional.ofNullable(currentPage).orElse(1);
        Pageable pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE, DEFAULT_SORT);

        return reminderRepository.getFiltered(
                authService.getUserId(),
                LocalDateTime.of(startDate, startTime),
                LocalDateTime.of(endDate, endTime),
                pageRequest
        );
    }

    public Page<Reminder> getList(Integer current, Integer total) {

        Pageable pageable = PageRequest.of(current, total, DEFAULT_SORT);

        return reminderRepository.getList(authService.getUserId(), pageable);
    }

    public void update(ReminderRq request, long id) {
        Reminder reminder = mapReminder(request);
        reminder.setId(id);

        reminderRepository.save(reminder);
    }

    public void delete(int id) {
        reminderRepository.deleteReminderById(id);
    }

    private Reminder mapReminder(ReminderRq request) {
        return Reminder.builder()
                .remindDateTime(request.getRemind())
                .title(request.getTitle())
                .description(request.getDescription())
                .user(userRepository.getReferenceById(authService.getUserId()))
                .build();
    }
}
