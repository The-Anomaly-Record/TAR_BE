package com.ultimate.the_anomaly_record.domain.auth.service;

import com.ultimate.the_anomaly_record.domain.auth.dto.EmailVerificationResponse;
import com.ultimate.the_anomaly_record.domain.auth.entity.EmailVerification;
import com.ultimate.the_anomaly_record.domain.auth.exception.EmailErrorCode;
import com.ultimate.the_anomaly_record.domain.auth.exception.EmailException;
import com.ultimate.the_anomaly_record.domain.auth.repository.EmailVerificationRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    //실제 이메일 발송 로직 추가
    private final JavaMailSender mailSender;


    public EmailVerificationResponse.SendCode sendVerificationCode(String email) {

        emailVerificationRepository.findTopByEmailOrderByExpiredTimeDesc(email)
                .ifPresent(emailVerification -> {
                    if (!emailVerification.isVerified() &&
                            emailVerification.getExpiredTime().isAfter(LocalDateTime.now())) {
                        emailVerificationRepository.delete(emailVerification);
                    }
                });

        String code = UUID.randomUUID().toString().substring(0, 6);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expiredTime(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        emailVerificationRepository.save(emailVerification);

        // 이메일 발송
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("회원가입 인증번호");
            message.setText("인증번호: " + code + "\n유효시간: 5분");
            mailSender.send(message);

            return EmailVerificationResponse.SendCode.builder()
                    .success(true)
                    .message("인증번호 전송 완료")
                    .build();
        } catch (Exception e) {

            return EmailVerificationResponse.SendCode.builder()
                    .success(false)
                    .message("이메일 전송 실패")
                    .build();
        }
    }

    private void validateEmailSyntax(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate(); // RFC 준수 문법 검사
        } catch (AddressException ex) {
            throw new EmailException(EmailErrorCode.INVALID_EMAIL_FORMAT); // 400 매핑
        }
    }

    public EmailVerificationResponse.VerifyCode verifyCode(String email, String code){
        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByExpiredTimeDesc(email)
                .filter(ev -> ev.getCode().equals(code))
                .orElseThrow(() -> new EmailException(EmailErrorCode.INVALID_CODE));

        if (emailVerification.isVerified()) {
            throw new EmailException(EmailErrorCode.ALREADY_VERIFIED);
        }

        if (emailVerification.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new EmailException(EmailErrorCode.EXPIRED);
        }

        emailVerification.setVerified(true);
        emailVerificationRepository.save(emailVerification);

        return EmailVerificationResponse.VerifyCode.builder()
                .success(true)
                .message("인증 성공")
                .build();
    }
}