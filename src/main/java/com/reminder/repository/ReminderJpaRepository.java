package com.reminder.repository;

import com.reminder.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderJpaRepository extends JpaRepository<Reminder, Long> {

    @Query("DELETE FROM Reminder r WHERE r.id = :id")
    void deleteReminderById(@Param("id") Long id);

    @Query("""
            SELECT r FROM Reminder r
            WHERE r.user.id = :userId
            AND r.remindDateTime >= :startDateTime
            AND r.remindDateTime <= :endDateTime
            """)
    Page<Reminder> getFiltered(@Param("userId") Long userId,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("endDateTime") LocalDateTime endDateTime,
                               Pageable pageRequest);

    @Query("""
            SELECT r FROM Reminder r JOIN FETCH r.user u
            WHERE r.remindDateTime >= :startDateTime
            AND r.remindDateTime <= :endDateTime
            """)
    List<Reminder> getRemindersForSend(@Param("startDateTime") LocalDateTime startDateTime,
                                       @Param("endDateTime") LocalDateTime endDateTime);

    @Query("""
            SELECT r FROM Reminder r
            WHERE r.user.id = :userId
            """)
    Page<Reminder> getList(@Param("userId") Long userId,
                           Pageable pageRequest);

    @Query("""
            SELECT r FROM Reminder r
            JOIN r.user
            WHERE r.id = :id
            """)
    Optional<Reminder> findByIdWithUser(@Param("id") Long id);
}
