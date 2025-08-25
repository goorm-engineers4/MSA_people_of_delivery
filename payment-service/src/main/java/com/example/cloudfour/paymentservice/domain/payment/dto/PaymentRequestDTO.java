package com.example.cloudfour.paymentservice.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class PaymentRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentConfirmRequestDTO {
        private String paymentKey;
        private String orderId;
        private Integer amount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCancelRequestDTO {
        private String cancelReason;
    }
}
