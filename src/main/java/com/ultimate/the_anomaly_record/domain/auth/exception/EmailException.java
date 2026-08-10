package com.ultimate.the_anomaly_record.domain.auth.exception;

import com.ultimate.the_anomaly_record.global.exception.GeneralException;

public class EmailException extends GeneralException {

    public EmailException(EmailErrorCode errorCode) {
        super(errorCode);
    }
}
