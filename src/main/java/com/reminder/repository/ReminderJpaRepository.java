package com.reminder.repository;

import com.reminder.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReminderJpaRepository extends JpaRepository<Reminder, Integer> {

    @Query("DELETE FROM Reminder r WHERE r.id = :id")
    void deleteReminderById(@Param("id") int id);

    @Query("""
            SELECT r FROM Reminder r
            WHERE r.user.id = :userId
            AND r.remindDateTime >= :startDateTime
            AND r.remindDateTime <= :endDateTime
            """)
    Page<Reminder> getFiltered(@Param("userId") int userId,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("endDateTime") LocalDateTime endDateTime,
                               Pageable pageRequest);



    @Query("""
            SELECT r FROM Reminder r
            WHERE r.user.id = :userId
            """)
    Page<Reminder> getList(@Param("userId") int userId,
                           Pageable pageRequest);
}
