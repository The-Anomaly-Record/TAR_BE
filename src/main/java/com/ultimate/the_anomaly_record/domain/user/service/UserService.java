package com.ultimate.the_anomaly_record.domain.user.service;

import com.ultimate.the_anomaly_record.domain.auth.entity.EmailVerification;
import com.ultimate.the_anomaly_record.domain.auth.exception.EmailErrorCode;
import com.ultimate.the_anomaly_record.domain.auth.exception.EmailException;
import com.ultimate.the_anomaly_record.domain.auth.repository.EmailVerificationRepository;
import com.ultimate.the_anomaly_record.domain.user.dto.UserRequest;
import com.ultimate.the_anomaly_record.domain.user.dto.UserResponse;
import com.ultimate.the_anomaly_record.domain.user.entity.User;
import com.ultimate.the_anomaly_record.domain.user.entity.enums.LoginType;
import com.ultimate.the_anomaly_record.domain.user.entity.enums.UserStatus;
import com.ultimate.the_anomaly_record.domain.user.exception.UserException;
import com.ultimate.the_anomaly_record.domain.user.repository.UserRepository;
import com.ultimate.the_anomaly_record.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입
    @Transactional
    public Long signup(UserRequest.SignupRequest dto) {
        final String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        // 최신 이메일 인증 기록 가져오기
        EmailVerification ev = emailVerificationRepository
                .findTopByEmailOrderByExpiredTimeDesc(email)
                .orElseThrow(() -> new EmailException(EmailErrorCode.NOT_VERIFIED));

        if (!ev.isVerified() || ev.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new EmailException(EmailErrorCode.NOT_VERIFIED);
        }

        // 이미 가입된 계정인지 확인
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .loginType(LoginType.NORMAL)
                .status(UserStatus.ACTIVE)
                .build();

        try {
            User saved = userRepository.save(user);
            log.info("[Signup] 회원가입 완료: {}", saved.getEmail());
            return saved.getId();
        } catch (DataIntegrityViolationException dup) {
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        } catch (Exception e) {
            log.error("[Signup] 회원가입 중 예기치 못한 오류", e);
            throw new UserException(ErrorStatus.INTERNAL_ERROR);
        }
    }

    // 로그인
    @Transactional(readOnly = true)
    public UserResponse.LoginResponse login(UserRequest.LoginRequest dto){
        final String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        if(user.getLoginType() != LoginType.NORMAL){
            throw new UserException(ErrorStatus.USER_NOT_FOUND);
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserException(ErrorStatus.USER_INACTIVE);
        }
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }
        return UserResponse.LoginResponse.builder()
                .email(user.getEmail())
                .build();

    }
}
