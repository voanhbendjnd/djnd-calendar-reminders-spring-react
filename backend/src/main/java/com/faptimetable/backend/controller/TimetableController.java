package com.faptimetable.backend.controller;

import com.faptimetable.backend.domain.entity.TimetableEntry;
import com.faptimetable.backend.domain.entity.User;
import com.faptimetable.backend.dto.ImportResultDTO;
import com.faptimetable.backend.dto.TimetableEntryDTO;
import com.faptimetable.backend.security.SecurityUser;
import com.faptimetable.backend.service.TimetableImportService;
import com.faptimetable.backend.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableImportService importService;
    private final TimetableService timetableService;

    @PostMapping("/import")
    public ResponseEntity<ImportResultDTO> importHtml(
            @RequestBody String html,
            @AuthenticationPrincipal SecurityUser securityUser) {
        return ResponseEntity.ok(importService.importFromHtml(html, securityUser.getUser()));
    }

    @PostMapping("/import/json")
    public ResponseEntity<ImportResultDTO> importJson(
            @RequestBody List<TimetableEntryDTO> entries,
            @AuthenticationPrincipal SecurityUser securityUser) {
        return ResponseEntity.ok(importService.importFromJson(entries, securityUser.getUser()));
    }

    @GetMapping("/week")
    public ResponseEntity<List<TimetableEntry>> getWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal SecurityUser securityUser) {
        return ResponseEntity.ok(timetableService.getWeek(date, securityUser.getUser()));
    }

    @GetMapping("/day")
    public ResponseEntity<List<TimetableEntry>> getDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal SecurityUser securityUser) {
        return ResponseEntity.ok(timetableService.getDay(date, securityUser.getUser()));
    }

    @DeleteMapping("/week")
    public ResponseEntity<Void> deleteWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal SecurityUser securityUser) {
        timetableService.deleteWeek(date, securityUser.getUser());
        return ResponseEntity.noContent().build();
    }
}
