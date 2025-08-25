package com.example.cloudfour.paymentservice.domain.payment.exception;

import com.example.cloudfour.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.modulecommon.apiPayLoad.exception.CustomException;

public class PaymentHistoryException extends CustomException {
    public PaymentHistoryException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
