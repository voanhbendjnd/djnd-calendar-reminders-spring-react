package com.faptimetable.backend.repository;

import com.faptimetable.backend.domain.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, UUID> {
    
    List<TimetableEntry> findByUserIdAndDateBetweenOrderByDateAscStartTimeAsc(UUID userId, LocalDate startDate, LocalDate endDate);
    
    List<TimetableEntry> findByUserIdAndDateOrderByStartTimeAsc(UUID userId, LocalDate date);
    
    Optional<TimetableEntry> findByUserIdAndDateAndSlotAndSubjectCodeAndClassName(UUID userId, LocalDate date, int slot, String subjectCode, String className);
    
    @Modifying
    @Query("DELETE FROM TimetableEntry t WHERE t.userId = :userId AND t.date BETWEEN :startDate AND :endDate")
    void deleteByUserIdAndDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
}
