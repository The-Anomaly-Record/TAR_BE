package com.ultimate.the_anomaly_record.domain.user.service;

import com.ultimate.the_anomaly_record.domain.auth.entity.EmailVerification;
import com.ultimate.the_anomaly_record.domain.auth.entity.RefreshToken;
import com.ultimate.the_anomaly_record.domain.auth.exception.EmailErrorCode;
import com.ultimate.the_anomaly_record.domain.auth.exception.EmailException;
import com.ultimate.the_anomaly_record.domain.auth.exception.JwtErrorCode;
import com.ultimate.the_anomaly_record.domain.auth.exception.JwtException;
import com.ultimate.the_anomaly_record.domain.auth.exception.RefreshTokenErrorCode;
import com.ultimate.the_anomaly_record.domain.auth.exception.RefreshTokenException;
import com.ultimate.the_anomaly_record.domain.auth.repository.EmailVerificationRepository;
import com.ultimate.the_anomaly_record.domain.auth.repository.RefreshTokenRepository;
import com.ultimate.the_anomaly_record.domain.user.dto.UserRequest;
import com.ultimate.the_anomaly_record.domain.user.dto.UserResponse;
import com.ultimate.the_anomaly_record.domain.user.entity.User;
import com.ultimate.the_anomaly_record.domain.user.entity.enums.LoginType;
import com.ultimate.the_anomaly_record.domain.user.entity.enums.UserStatus;
import com.ultimate.the_anomaly_record.domain.user.exception.UserErrorCode;
import com.ultimate.the_anomaly_record.domain.user.exception.UserException;
import com.ultimate.the_anomaly_record.domain.user.repository.UserRepository;
import com.ultimate.the_anomaly_record.global.response.ErrorStatus;
import com.ultimate.the_anomaly_record.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
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
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
        } catch (Exception e) {
            log.error("[Signup] 회원가입 중 예기치 못한 오류", e);
            throw new UserException(ErrorStatus.INTERNAL_ERROR);
        }
    }

    // 로그인
    @Transactional
    public UserResponse.LoginResponse login(String email, String password) {
        log.info("[Login] 시도: {}", email);

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            log.error("[Login] 이메일 또는 비밀번호 비어있음");
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        // 인증
        try {
            log.info("[Login] AuthenticationManager를 통한 인증 시도: {}", email);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (authentication == null || !authentication.isAuthenticated()) {
                log.error("[Login] 인증 실패: {}", email);
                throw new UserException(ErrorStatus.INVALID_INPUT);
            }

            log.info("[Login] 인증 성공: {}", email);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.error("[Login] 비밀번호 불일치: {}", email);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.error("[Login] 사용자를 찾을 수 없음: {}", email);
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("[Login] 인증 중 오류: {}", e.getMessage(), e);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        // 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[Login] 사용자 없음: {}", email);
                    return new UserException(UserErrorCode.USER_NOT_FOUND);
                });

        // 토큰 생성
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtUtil.generateToken(user.getEmail());
            refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            log.info("[Login] 토큰 생성 성공");
        } catch (Exception e) {
            log.error("[Login] 토큰 생성 오류: {}", e.getMessage(), e);
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }

        // Refresh Token 저장
        try {
            refreshTokenRepository.findByUserId(user.getId())
                    .ifPresent(refreshTokenRepository::delete);

            refreshTokenRepository.save(RefreshToken.builder()
                    .userId(user.getId())
                    .token(refreshToken)
                    .build());

            log.info("[Login] Refresh Token 저장 완료");
        } catch (Exception e) {
            log.error("[Login] Refresh Token 저장 오류: {}", e.getMessage(), e);
            throw new RefreshTokenException(RefreshTokenErrorCode.INVALID);
        }

        return UserResponse.LoginResponse.builder()
                .email(user.getEmail())
                .message("로그인 성공")
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
