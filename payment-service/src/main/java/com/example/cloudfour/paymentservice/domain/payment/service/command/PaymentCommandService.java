package com.example.cloudfour.paymentservice.domain.payment.service.command;

import java.util.UUID;

public interface PaymentCommandService {
    String createPayment(String request, UUID userId);
    String verifyPayment(String request, UUID userId);
    String updatePayment(String request, UUID orderId, UUID userId);
    String cancelPayment(String request, UUID orderId, UUID userId);
    void recordPaymentFail(String orderId, String message);
    void updateStatusFromWebhook(String payload);
}
