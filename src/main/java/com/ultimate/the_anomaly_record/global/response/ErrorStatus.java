package com.ultimate.the_anomaly_record.global.response;

import com.ultimate.the_anomaly_record.global.exception.BaseErrorCode;

public enum ErrorStatus implements BaseErrorCode {
    INVALID_INPUT("C001", "입력 값이 유효하지 않습니다."),
    INTERNAL_ERROR("S001", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
