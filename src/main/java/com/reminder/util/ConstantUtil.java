package com.reminder.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@UtilityClass
public class ConstantUtil {
    @Value("app.pagination.default-page-size:10")
    public static int DEFAULT_PAGE_SIZE;

    public static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("remindDateTime"));
    public static final LocalTime DEFAULT_TIME = LocalTime.of(0, 0);
    public static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);
    public static final LocalDate MAX_DATE = LocalDate.of(3000, 1, 1);

    public static Sort.Order getDateTimeOrder(String dateTime) {
        return Optional.ofNullable(dateTime)
                .map(dateDirection -> dateDirection.equals("asc") ?
                        Sort.Order.asc("remindDateTime") :
                        Sort.Order.desc("remindDateTime"))
                .orElse(Sort.Order.asc("remindDateTime"));
    }

    public static Sort.Order getTitleOrder(String title) {
        return Optional.ofNullable(title)
                .map(nameDirection -> nameDirection.equals("asc") ?
                        Sort.Order.asc("title") :
                        Sort.Order.desc("title"))
                .orElse(Sort.Order.asc("title"));
    }
}
