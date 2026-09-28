package com.faptimetable.backend.service;

import com.faptimetable.backend.domain.entity.TimetableEntry;
import com.faptimetable.backend.domain.entity.User;
import com.faptimetable.backend.repository.TimetableEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableEntryRepository repository;

    @Transactional(readOnly = true)
    public List<TimetableEntry> getWeek(LocalDate date, User user) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return repository.findByUserIdAndDateBetweenOrderByDateAscStartTimeAsc(user.getId(), startOfWeek, endOfWeek);
    }

    @Transactional(readOnly = true)
    public List<TimetableEntry> getDay(LocalDate date, User user) {
        return repository.findByUserIdAndDateOrderByStartTimeAsc(user.getId(), date);
    }

    @Transactional
    public void deleteWeek(LocalDate date, User user) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        repository.deleteByUserIdAndDateBetween(user.getId(), startOfWeek, endOfWeek);
    }
}
