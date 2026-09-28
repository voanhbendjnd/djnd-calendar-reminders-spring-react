package com.faptimetable.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class TimetableEntryDTO {
    private LocalDate date;
    private int slot;
    private String subjectCode;
    private String className;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mode;
    private String room;
    private String lecturer;
    private String meetingUrl;
}
