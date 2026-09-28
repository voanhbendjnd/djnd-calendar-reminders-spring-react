package com.faptimetable.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "user_notification_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true, nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private boolean enabled;

    @Builder.Default
    @Column(nullable = false)
    private LocalTime notificationTime = LocalTime.of(5, 0);

    @Builder.Default
    @Column(nullable = false)
    private String timezone = "Asia/Ho_Chi_Minh";
}
