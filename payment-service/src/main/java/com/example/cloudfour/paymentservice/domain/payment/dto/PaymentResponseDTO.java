package com.example.cloudfour.paymentservice.domain.payment.dto;

import com.example.cloudfour.paymentservice.domain.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class PaymentResponseDTO {

    @Getter @Builder
    public static class PaymentCreateResponseDTO {
        private String paymentUrl;
    }

    @Getter @Builder
    public static class PaymentVerifyResponseDTO {
        private String paymentKey;
        private String orderId;
        private Integer amount;
        private String paymentMethod;
        private PaymentStatus paymentStatus;
    }

    @Getter @Builder
    public static class PaymentUpdateResponseDTO {
        private UUID paymentId;
        private PaymentStatus updatedStatus;
    }

    @Getter @Builder
    public static class PaymentCancelResponseDTO {
        private UUID paymentId;
        private PaymentStatus status;
        private String cancelReason;
    }

    @Getter @Builder
    public static class PaymentDetailResponseDTO {
        private String paymentKey;
        private String orderId;
        private Integer amount;
        private String paymentMethod;
        private PaymentStatus paymentStatus;
        private String failReason;
    }

    @Getter @Builder
    public static class PaymentUserListResponseDTO {
        private List<PaymentDetailResponseDTO> paymentList;
    }

    @Getter @Builder
    public static class PaymentStoreListResponseDTO {
        private List<PaymentDetailResponseDTO> paymentList;
    }

    @Getter @Builder
    public static class PaymentStoreSummaryResponseDTO {
        private Integer totalSales;
        private Integer totalCount;
    }
}

