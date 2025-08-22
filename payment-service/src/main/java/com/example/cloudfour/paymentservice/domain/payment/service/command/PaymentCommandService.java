package com.example.cloudfour.paymentservice.domain.payment.service.command;

import com.example.cloudfour.paymentservice.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.paymentservice.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.paymentservice.domain.payment.dto.TossWebhookPayload;

import java.util.UUID;

public interface PaymentCommandService {
    PaymentResponseDTO.PaymentCreateResponseDTO createPayment(PaymentRequestDTO.PaymentCreateRequestDTO request, UUID userId);
    PaymentResponseDTO.PaymentVerifyResponseDTO verifyPayment(PaymentRequestDTO.PaymentVerifyRequestDTO request, UUID userId);
    PaymentResponseDTO.PaymentUpdateResponseDTO updatePayment(PaymentRequestDTO.PaymentUpdateRequestDTO request, UUID orderId, UUID userId);
    PaymentResponseDTO.PaymentCancelResponseDTO cancelPayment(PaymentRequestDTO.PaymentCancelRequestDTO request, UUID orderId, UUID userId);
    void recordPaymentFail(String orderId, String message);
    void updateStatusFromWebhook(TossWebhookPayload payload);
}
