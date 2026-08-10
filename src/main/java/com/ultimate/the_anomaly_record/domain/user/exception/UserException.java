package com.ultimate.the_anomaly_record.domain.user.exception;

import com.ultimate.the_anomaly_record.global.exception.BaseErrorCode;
import com.ultimate.the_anomaly_record.global.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
