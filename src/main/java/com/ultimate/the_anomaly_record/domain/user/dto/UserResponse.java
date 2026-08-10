package com.ultimate.the_anomaly_record.domain.user.dto;

import lombok.*;

public class UserResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupResponse {
        private Long id;
        private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginResponse {
        private String email;
        private String message;

        private String token;
        private String refreshToken;
    }
}
