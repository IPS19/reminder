package com.reminder.service;

import com.reminder.annotation.CheckUserOwnsReminder;
import com.reminder.entity.Reminder;
import com.reminder.model.AuthUser;
import com.reminder.model.ReminderRq;
import com.reminder.repository.ReminderJpaRepository;
import com.reminder.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import static com.reminder.util.ConstantUtil.DEFAULT_SORT;
import static com.reminder.util.ConstantUtil.DEFAULT_TIME;
import static com.reminder.util.ConstantUtil.MAX_DATE;
import static com.reminder.util.ConstantUtil.MIN_DATE;
import static com.reminder.util.ConstantUtil.getDateTimeOrder;
import static com.reminder.util.ConstantUtil.getTitleOrder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderJpaRepository reminderRepository;

    private final UserJpaRepository userRepository;
    @Value("${app.pagination.default-page-size:10}")
    public int DEFAULT_PAGE_SIZE;

    public Reminder saveNew(ReminderRq request) {
        Reminder reminder = mapReminder(request);
        Long currentUserId = AuthUser.get().id();
        reminder.setUser(userRepository.getReferenceById(currentUserId));
        log.info("добавляем новое напоминание - лог с сервиса пум пум");

        return reminderRepository.save(reminder);
    }

    public Page<Reminder> getSorted(String dateTime, String title, Integer currentPage) {

        Sort.Order dateTimeOrder = getDateTimeOrder(dateTime);

        Sort.Order titleOrder = getTitleOrder(title);

        Sort sort = Sort.by(dateTimeOrder, titleOrder);

        Integer page = Optional.ofNullable(currentPage).orElse(0);

        Pageable pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE, sort);

        Long id = AuthUser.get().id();
        Page<Reminder> list = reminderRepository.getList(id, pageRequest);
        MDC.put("userId", id.toString());
        return list;
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

        Integer page = Optional.ofNullable(currentPage).orElse(0);
        Pageable pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE, DEFAULT_SORT);

        return reminderRepository.getFiltered(
                AuthUser.get().id(),
                LocalDateTime.of(startDate, startTime),
                LocalDateTime.of(endDate, endTime),
                pageRequest
        );
    }

    public Page<Reminder> getList(Integer current, Integer total) {

        Pageable pageable = PageRequest.of(current, total, DEFAULT_SORT);

        return reminderRepository.getList(AuthUser.get().id(), pageable);
    }

    @CheckUserOwnsReminder(reminderIdParam = "id")
    public void update(ReminderRq request, long id) {
        Reminder reminder = mapReminder(request);
        reminder.setId(id);

        reminderRepository.save(reminder);
    }

    @CheckUserOwnsReminder(reminderIdParam = "id")
    public void delete(Long id) {
        reminderRepository.deleteReminderById(id);
    }

    private Reminder mapReminder(ReminderRq request) {
        return Reminder.builder()
                .remindDateTime(request.getRemind())
                .title(request.getTitle())
                .description(request.getDescription())
                .user(userRepository.getReferenceById(AuthUser.get().id()))
                .build();
    }
}
