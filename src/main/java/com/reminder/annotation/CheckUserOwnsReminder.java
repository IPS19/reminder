package com.reminder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUserOwnsReminder {
    // Имя параметра метода, содержащего reminderId
    String reminderIdParam() default "reminderId";
}
