package com.faptimetable.backend.service;

import com.faptimetable.backend.domain.entity.TimetableEntry;
import com.faptimetable.backend.domain.entity.User;
import com.faptimetable.backend.dto.ImportResultDTO;
import com.faptimetable.backend.dto.TimetableEntryDTO;
import com.faptimetable.backend.parser.FapTimetableParser;
import com.faptimetable.backend.repository.TimetableEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimetableImportService {

    private final FapTimetableParser parser;
    private final TimetableEntryRepository repository;

    @Transactional
    public ImportResultDTO importFromHtml(String html, User user) {
        List<TimetableEntryDTO> entries = parser.parse(html);
        return importEntries(entries, user);
    }

    @Transactional
    public ImportResultDTO importFromJson(List<TimetableEntryDTO> entries, User user) {
        return importEntries(entries, user);
    }

    private ImportResultDTO importEntries(List<TimetableEntryDTO> entries, User user) {
        int imported = 0;
        int updated = 0;
        int skipped = 0;
        LocalDate weekStart = null;
        LocalDate weekEnd = null;

        for (TimetableEntryDTO dto : entries) {
            if (weekStart == null || dto.getDate().isBefore(weekStart)) {
                weekStart = dto.getDate();
            }
            if (weekEnd == null || dto.getDate().isAfter(weekEnd)) {
                weekEnd = dto.getDate();
            }

            Optional<TimetableEntry> existing = repository.findByUserIdAndDateAndSlotAndSubjectCodeAndClassName(
                    user.getId(), dto.getDate(), dto.getSlot(), dto.getSubjectCode(), dto.getClassName()
            );

            if (existing.isPresent()) {
                TimetableEntry entry = existing.get();
                // Update if fields changed
                if (!entry.getStartTime().equals(dto.getStartTime()) ||
                    !entry.getEndTime().equals(dto.getEndTime()) ||
                    !entry.getMode().equals(dto.getMode()) ||
                    (entry.getRoom() != null && !entry.getRoom().equals(dto.getRoom())) ||
                    (entry.getLecturer() != null && !entry.getLecturer().equals(dto.getLecturer())) ||
                    (entry.getMeetingUrl() != null && !entry.getMeetingUrl().equals(dto.getMeetingUrl()))) {
                    
                    entry.setStartTime(dto.getStartTime());
                    entry.setEndTime(dto.getEndTime());
                    entry.setMode(dto.getMode());
                    entry.setRoom(dto.getRoom());
                    entry.setLecturer(dto.getLecturer());
                    entry.setMeetingUrl(dto.getMeetingUrl());
                    repository.save(entry);
                    updated++;
                } else {
                    skipped++;
                }
            } else {
                TimetableEntry entry = TimetableEntry.builder()
                        .userId(user.getId())
                        .date(dto.getDate())
                        .slot(dto.getSlot())
                        .subjectCode(dto.getSubjectCode())
                        .className(dto.getClassName())
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .mode(dto.getMode())
                        .room(dto.getRoom())
                        .lecturer(dto.getLecturer())
                        .meetingUrl(dto.getMeetingUrl())
                        .build();
                repository.save(entry);
                imported++;
            }
        }

        return ImportResultDTO.builder()
                .success(true)
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .imported(imported)
                .updated(updated)
                .skipped(skipped)
                .build();
    }
}
