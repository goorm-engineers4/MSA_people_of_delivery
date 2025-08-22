package com.example.cloudfour.paymentservice.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossErrorResponse {

    private String code;
    private String message;
    private String orderId;
    private String paymentKey;
    private String status;
}