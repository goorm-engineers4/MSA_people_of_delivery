package com.example.cloudfour.paymentservice.domain.payment.service.query;

import java.util.UUID;

public interface PaymentQueryService {
    String getDetailPayment(UUID orderId, UUID userId);
    String getStoreListPayment(UUID storeId, UUID userId);
    String getUserListPayment(UUID userId);
    String getStoreSummaryPayment(UUID storeId, UUID userId);
}
