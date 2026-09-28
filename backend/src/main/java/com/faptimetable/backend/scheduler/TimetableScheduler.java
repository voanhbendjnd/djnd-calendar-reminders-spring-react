package com.faptimetable.backend.scheduler;

import com.faptimetable.backend.service.TimetableNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimetableScheduler {

    private final TimetableNotificationService notificationService;

    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Ho_Chi_Minh")
    public void scheduleDailyEmails() {
        notificationService.sendDailyTimetableEmails();
    }
}
