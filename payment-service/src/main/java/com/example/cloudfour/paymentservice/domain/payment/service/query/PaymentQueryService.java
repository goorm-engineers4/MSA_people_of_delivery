package com.example.cloudfour.paymentservice.domain.payment.service.query;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;

import java.util.UUID;

public interface PaymentQueryService {
    PaymentResponseDTO.PaymentDetailResponseDTO  getDetailPayment(UUID orderId, UUID userId);
    PaymentResponseDTO.PaymentStoreListResponseDTO  getStoreListPayment(UUID storeId, UUID userId);
    PaymentResponseDTO.PaymentUserListResponseDTO  getUserListPayment(UUID userId);
    PaymentResponseDTO.PaymentStoreSummaryResponseDTO getStoreSummaryPayment(UUID storeId, UUID userId);
}
