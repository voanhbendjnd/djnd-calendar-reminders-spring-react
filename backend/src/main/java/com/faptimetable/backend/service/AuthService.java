package com.faptimetable.backend.service;

import com.faptimetable.backend.domain.entity.User;
import com.faptimetable.backend.domain.entity.UserNotificationSetting;
import com.faptimetable.backend.dto.AuthRequest;
import com.faptimetable.backend.dto.AuthResponse;
import com.faptimetable.backend.dto.RegisterRequest;
import com.faptimetable.backend.repository.UserNotificationSettingRepository;
import com.faptimetable.backend.repository.UserRepository;
import com.faptimetable.backend.security.JwtUtil;
import com.faptimetable.backend.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();
        userRepository.save(user);

        // default notification settings
        UserNotificationSetting settings = UserNotificationSetting.builder()
                .userId(user.getId())
                .enabled(false)
                .notificationTime(java.time.LocalTime.of(5, 0))
                .timezone("Asia/Ho_Chi_Minh")
                .build();
        userNotificationSettingRepository.save(settings);

        SecurityUser securityUser = new SecurityUser(user);
        var jwtToken = jwtUtil.generateToken(securityUser);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        SecurityUser securityUser = new SecurityUser(user);
        var jwtToken = jwtUtil.generateToken(securityUser);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .build();
    }
}
