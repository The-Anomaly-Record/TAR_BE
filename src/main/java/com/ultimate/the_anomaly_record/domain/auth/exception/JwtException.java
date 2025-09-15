package com.ultimate.the_anomaly_record.domain.auth.exception;

public class JwtException extends RuntimeException {

    private final JwtErrorCode errorCode;

    public JwtException(JwtErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public JwtErrorCode getErrorCode() {
        return errorCode;
    }
}