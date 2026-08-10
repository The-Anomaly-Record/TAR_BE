package com.ultimate.the_anomaly_record.domain.auth.exception;

import com.ultimate.the_anomaly_record.global.exception.GeneralException;

public class RefreshTokenException extends GeneralException {

    public RefreshTokenException(RefreshTokenErrorCode errorCode) {
        super(errorCode);
    }
}
