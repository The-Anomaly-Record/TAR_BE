package com.ultimate.the_anomaly_record.domain.user.exception;

import com.ultimate.the_anomaly_record.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND("U001", "해당 유저를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("U002", "이미 존재하는 유저입니다."),
    USER_INACTIVE("U003", "가입은 되어있으나 현재 로그인할 수 없는 상태입니다.");

    private final String code;
    private final String message;
}
