package com.faptimetable.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "timetable_entries",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date", "slot", "subject_code", "class_name"})
    },
    indexes = {
        @Index(name = "idx_timetable_user_date", columnList = "user_id, date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int slot;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String mode; // OFFLINE, ONLINE

    private String room;
    private String lecturer;
    private String meetingUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
