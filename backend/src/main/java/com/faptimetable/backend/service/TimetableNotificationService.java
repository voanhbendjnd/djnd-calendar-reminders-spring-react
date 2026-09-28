package com.faptimetable.backend.service;

import com.faptimetable.backend.domain.entity.TimetableEntry;
import com.faptimetable.backend.domain.entity.User;
import com.faptimetable.backend.domain.entity.UserNotificationSetting;
import com.faptimetable.backend.repository.TimetableEntryRepository;
import com.faptimetable.backend.repository.UserNotificationSettingRepository;
import com.faptimetable.backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableNotificationService {

    private final UserNotificationSettingRepository settingsRepository;
    private final TimetableEntryRepository timetableRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public void sendDailyTimetableEmails() {
        List<UserNotificationSetting> activeSettings = settingsRepository.findByEnabledTrue();
        LocalDate today = LocalDate.now();

        for (UserNotificationSetting setting : activeSettings) {
            User user = userRepository.findById(setting.getUserId()).orElse(null);
            if (user == null || !user.isEnabled()) continue;

            List<TimetableEntry> todaysClasses = timetableRepository.findByUserIdAndDateOrderByStartTimeAsc(user.getId(), today);
            
            try {
                sendEmail(user.getEmail(), today, todaysClasses);
            } catch (Exception e) {
                System.err.println("Failed to send email to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    private void sendEmail(String to, LocalDate date, List<TimetableEntry> classes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Today's timetable - " + date.getDayOfWeek().name() + ", " + date.getMonth().name() + " " + date.getDayOfMonth());

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<p>Good morning!</p>");

        if (classes.isEmpty()) {
            html.append("<p>You have no classes today.</p>");
        } else {
            html.append("<p>You have ").append(classes.size()).append(" classes today.</p>");
            
            for (TimetableEntry entry : classes) {
                html.append("<hr style='margin: 20px 0;'>");
                html.append("<p><strong>").append(entry.getStartTime()).append(" - ").append(entry.getEndTime()).append("</strong><br>");
                html.append("<strong>").append(entry.getSubjectCode()).append("</strong><br>");
                html.append(entry.getClassName()).append("<br>");
                if (entry.getRoom() != null) html.append(entry.getRoom()).append("<br>");
                html.append(entry.getMode()).append("<br>");
                if (entry.getLecturer() != null) html.append("Lecturer: ").append(entry.getLecturer()).append("</p>");
                if ("ONLINE".equals(entry.getMode()) && entry.getMeetingUrl() != null) {
                    html.append("<p><a href='").append(entry.getMeetingUrl()).append("' style='padding: 5px 10px; background-color: #28a745; color: white; text-decoration: none; border-radius: 3px;'>Join meeting</a></p>");
                }
            }
            html.append("<hr style='margin: 20px 0;'>");
        }

        html.append("</body></html>");
        helper.setText(html.toString(), true);
        
        mailSender.send(message);
    }
}
