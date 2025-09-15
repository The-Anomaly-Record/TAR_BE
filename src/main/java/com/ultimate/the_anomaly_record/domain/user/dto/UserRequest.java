package com.ultimate.the_anomaly_record.domain.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRequest {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupRequest {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequest {
        private String email;
        private String password;
    }
}
