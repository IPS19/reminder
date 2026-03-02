package com.reminder.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;

@UtilityClass
public class ConstantUtil {
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("remindDateTime"));
    public static final LocalTime DEFAULT_TIME = LocalTime.of(0, 0);
    public static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);
    public static final LocalDate MAX_DATE = LocalDate.of(3000, 1, 1);
}
