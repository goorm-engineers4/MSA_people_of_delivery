package com.example.cloudfour.paymentservice.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TossCancelResponse {

    private String paymentKey;
    private String orderId;
    private String status;
    private List<Cancel> cancels;
    private String version;
    private String type;
    private String country;
    private String currency;
    private Integer totalAmount;
    private Integer balanceAmount;
    private Integer suppliedAmount;
    private Integer vat;
    private Integer taxFreeAmount;
    private Integer taxExemptionAmount;


    @Getter
    @Builder
    public static class Cancel {
        private Integer cancelAmount;
        private String cancelReason;
        private Integer taxFreeAmount;
        private Integer taxExemptionAmount;
        private Integer refundableAmount;
        private Integer easyPayDiscountAmount;
        private LocalDateTime canceledAt;
        private String transactionKey;
        private String receiptKey;
    }
}
