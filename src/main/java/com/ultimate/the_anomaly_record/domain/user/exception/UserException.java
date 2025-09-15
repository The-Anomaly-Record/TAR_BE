package com.ultimate.the_anomaly_record.domain.user.exception;


import com.ultimate.the_anomaly_record.global.apiPayload.code.status.ErrorStatus;
import com.ultimate.the_anomaly_record.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(ErrorStatus errorStatus){
        super(errorStatus);
    }
}
