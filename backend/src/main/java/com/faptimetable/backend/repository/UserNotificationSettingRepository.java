package com.faptimetable.backend.repository;

import com.faptimetable.backend.domain.entity.UserNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, UUID> {
    Optional<UserNotificationSetting> findByUserId(UUID userId);
    List<UserNotificationSetting> findByEnabledTrue();
}
