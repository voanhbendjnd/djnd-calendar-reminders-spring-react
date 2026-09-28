package com.faptimetable.backend.controller;

import com.faptimetable.backend.service.TimetableNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/jobs")
@RequiredArgsConstructor
public class InternalJobController {

    private final TimetableNotificationService notificationService;

    @Value("${internal.job.secret}")
    private String internalJobSecret;

    @PostMapping("/send-daily-timetable")
    public ResponseEntity<String> sendDailyTimetable(
            @RequestHeader(value = "X-Internal-Job-Secret", required = false) String providedSecret) {
        if (internalJobSecret == null || internalJobSecret.isEmpty()) {
            return ResponseEntity.status(500).body("Internal job secret not configured");
        }

        if (providedSecret == null || !providedSecret.equals(internalJobSecret)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        notificationService.sendDailyTimetableEmails();
        return ResponseEntity.ok("Job triggered successfully");
    }
}
