package com.faptimetable.backend.parser;

import com.faptimetable.backend.dto.TimetableEntryDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FapTimetableParser {

    public List<TimetableEntryDTO> parse(String html) {
        Document doc = Jsoup.parse(html);
        List<TimetableEntryDTO> entries = new ArrayList<>();

        // Locate the desktop table
        Element table = doc.selectFirst(".table-responsive.d-none.d-md-block.weekly-timetable-grid table");
        if (table == null) {
            throw new IllegalArgumentException("Could not find the timetable grid in the HTML");
        }

        // Parse column headers to get dates
        Elements headers = table.select("thead th");
        if (headers.size() < 8) {
            throw new IllegalArgumentException("Invalid table format: missing day columns");
        }

        List<LocalDate> columnDates = new ArrayList<>();
        columnDates.add(null); // index 0 is Slot column

        int currentYear = LocalDate.now().getYear(); // fallback to current year

        for (int i = 1; i < headers.size(); i++) {
            String headerText = headers.get(i).text();
            // Expected format: Thứ 2 (05/10) or Chủ nhật (11/10)
            Matcher m = Pattern.compile("\\((\\d{1,2})/(\\d{1,2})\\)").matcher(headerText);
            if (m.find()) {
                int day = Integer.parseInt(m.group(1));
                int month = Integer.parseInt(m.group(2));
                columnDates.add(LocalDate.of(currentYear, month, day));
            } else {
                columnDates.add(null);
            }
        }

        Elements rows = table.select("tbody tr");
        for (Element row : rows) {
            Element slotTh = row.selectFirst("th[scope=row]");
            if (slotTh == null) continue;
            
            int slot;
            try {
                slot = Integer.parseInt(slotTh.text().trim());
            } catch (NumberFormatException e) {
                continue;
            }

            Elements cells = row.select("td");
            for (int i = 0; i < cells.size() && (i + 1) < columnDates.size(); i++) {
                Element cell = cells.get(i);
                LocalDate date = columnDates.get(i + 1);
                
                if (date == null) continue;

                // Check if empty
                if (cell.selectFirst(".text-muted") != null && cell.text().trim().equals("-")) {
                    continue; // empty cell
                }

                // Check for class content
                Element classContent = cell.selectFirst(".position-relative");
                if (classContent == null) continue;

                TimetableEntryDTO.TimetableEntryDTOBuilder builder = TimetableEntryDTO.builder();
                builder.date(date);
                builder.slot(slot);

                // Subject and Class
                Element titleEl = classContent.selectFirst(".weekly-timetable-cell-title");
                if (titleEl != null) {
                    String titleText = titleEl.text().trim(); // e.g., "SWD392 - SE1910", "SWD392-EX - SE1910"
                    int lastDashIndex = titleText.lastIndexOf("-");
                    if (lastDashIndex != -1 && lastDashIndex != 0) {
                        builder.subjectCode(titleText.substring(0, lastDashIndex).trim());
                        builder.className(titleText.substring(lastDashIndex + 1).trim());
                    } else {
                        builder.subjectCode(titleText);
                        builder.className("");
                    }
                }

                // Time and Mode
                Element timeEl = classContent.selectFirst("div[title=Giờ bắt đầu - Giờ kết thúc]");
                if (timeEl != null) {
                    Element timeSpan = timeEl.selectFirst(".text-warning-emphasis");
                    if (timeSpan != null) {
                        String timeText = timeSpan.text().trim(); // e.g., "07h00-09h15"
                        String[] times = timeText.split("-");
                        if (times.length == 2) {
                            builder.startTime(parseTime(times[0]));
                            builder.endTime(parseTime(times[1]));
                        }
                    }
                    
                    // Check mode
                    if (timeEl.selectFirst(".text-success") != null || timeEl.text().contains("Online")) {
                        builder.mode("ONLINE");
                    } else {
                        builder.mode("OFFLINE");
                    }

                    // Meeting URL
                    Element linkEl = timeEl.selectFirst("a[title=Link Meet]");
                    if (linkEl != null) {
                        builder.meetingUrl(linkEl.attr("href"));
                    }
                }

                // Room
                Element roomEl = classContent.selectFirst("div[title=Phòng]");
                if (roomEl != null) {
                    builder.room(roomEl.text().trim());
                }

                // Lecturer
                Element lecturerEl = classContent.selectFirst("div[title=Giảng viên]");
                if (lecturerEl != null) {
                    String lecturerText = lecturerEl.text().trim();
                    if (!lecturerText.equals("-")) {
                        builder.lecturer(lecturerText);
                    }
                }

                entries.add(builder.build());
            }
        }
        return entries;
    }

    private LocalTime parseTime(String timeStr) {
        timeStr = timeStr.trim().replace("h", ":");
        if (timeStr.length() == 4) {
            timeStr = "0" + timeStr;
        }
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
