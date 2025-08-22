package com.example.cloudfour.paymentservice.domain.payment.exception;

import com.example.cloudfour.modulecommon.apiPayLoad.code.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {

    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT_400_1", "결제에 실패했습니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "PAYMENT_400_2", "취소할 수 없는 결제 상태입니다."),

    UNAUTHORIZED_PAYMENT_ACCESS(HttpStatus.FORBIDDEN, "PAYMENT_403_1", "결제 정보에 접근할 권한이 없습니다."),

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_404_1", "결제 정보를 찾을 수 없습니다."),

    PAYMENT_ALREADY_APPROVED(HttpStatus.CONFLICT, "PAYMENT_409_1", "이미 승인된 결제입니다."),

    TOSS_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_500_1", "외부 결제 API 연동 중 오류가 발생했습니다."),
    TOSS_STATUS_UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_500_2", "알 수 없는 Toss 결제 상태입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_500_3", "결제 처리 중 서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
