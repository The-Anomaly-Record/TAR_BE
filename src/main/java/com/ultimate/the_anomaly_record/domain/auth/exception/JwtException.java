package com.ultimate.the_anomaly_record.domain.auth.exception;

import com.ultimate.the_anomaly_record.global.exception.GeneralException;

public class JwtException extends GeneralException {

    public JwtException(JwtErrorCode errorCode) {
        super(errorCode);
    }
}
