package com.example.cloudfour.paymentservice.domain.payment.exception;

import com.example.cloudfour.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.modulecommon.apiPayLoad.exception.CustomException;

public class PaymentException extends CustomException {
    public PaymentException(BaseErrorCode errorCode) {
      super(errorCode);
    }
    public PaymentException(BaseErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
