package com.example.cloudfour.paymentservice.domain.payment.exception;

import com.example.cloudfour.modulecommon.apiPayLoad.code.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentHistoryErrorCode implements BaseErrorCode {

    PAYMENT_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_HISTORY_404_1", "결제 기록을 찾을 수 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_HISTORY_500_1", "결제 기록 처리 중 서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    PaymentHistoryErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
