package com.ultimate.the_anomaly_record.domain.auth.dto;

import lombok.*;

public class EmailVerificationRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendCode {
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerifyCode {
        private String email;
        private String code;
    }
}
