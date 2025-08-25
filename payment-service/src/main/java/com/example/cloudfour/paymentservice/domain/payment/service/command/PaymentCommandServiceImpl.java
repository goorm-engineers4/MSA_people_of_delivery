package com.example.cloudfour.paymentservice.domain.payment.service.command;

import com.example.cloudfour.paymentservice.domain.payment.converter.PaymentConverter;
import com.example.cloudfour.paymentservice.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.paymentservice.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.paymentservice.domain.payment.entity.Payment;
import com.example.cloudfour.paymentservice.domain.payment.entity.PaymentHistory;
import com.example.cloudfour.paymentservice.domain.payment.enums.PaymentStatus;
import com.example.cloudfour.paymentservice.domain.payment.exception.PaymentErrorCode;
import com.example.cloudfour.paymentservice.domain.payment.exception.PaymentException;
import com.example.cloudfour.paymentservice.domain.payment.repository.PaymentHistoryRepository;
import com.example.cloudfour.paymentservice.domain.payment.repository.PaymentRepository;
import com.example.cloudfour.paymentservice.domain.payment.service.IdempotencyService;
import com.example.cloudfour.paymentservice.domain.payment.service.WebhookSignatureService;
import com.example.cloudfour.paymentservice.domain.payment.apiclient.OrderClient;
import com.example.cloudfour.paymentservice.domain.payment.apiclient.TossApiClient;
import com.example.cloudfour.paymentservice.domain.payment.dto.TossWebhookPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final TossApiClient tossApiClient;
    private final IdempotencyService idempotencyService;
    private final WebhookSignatureService webhookSignatureService;
    private final PaymentConverter paymentConverter;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentResponseDTO.PaymentConfirmResponseDTO confirmPayment(PaymentRequestDTO.PaymentConfirmRequestDTO request, UUID userId) {
        log.info("결제 승인 시작: paymentKey={}, orderId={}, userId={}", request.getPaymentKey(), request.getOrderId(), userId);

        var existingPayment = idempotencyService.checkPaymentApprovalIdempotency(request.getPaymentKey(), request.getOrderId());
        if (existingPayment.isPresent()) {
            log.info("중복 결제 승인 요청 무시: paymentKey={}, orderId={}", request.getPaymentKey(), request.getOrderId());
            Payment payment = existingPayment.get();
            return paymentConverter.toConfirmResponse(payment);
        }

        String idempotencyKey = UUID.nameUUIDFromBytes(
            (request.getPaymentKey() + request.getOrderId()).getBytes()
        ).toString();

        try {
            TossApiClient.TossApproveResponse tossResponse = tossApiClient.approvePayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount(),
                idempotencyKey
            );

            Payment payment = Payment.builder()
                    .paymentKey(tossResponse.paymentKey)
                    .orderId(UUID.fromString(tossResponse.orderId))
                    .userId(userId)
                    .amount(tossResponse.totalAmount)
                    .paymentMethod(tossResponse.method)
                    .paymentStatus(PaymentStatus.APPROVED)
                    .approvedAt(LocalDateTime.now())
                    .rawResponse(objectMapper.writeValueAsString(tossResponse))
                    .build();

            idempotencyService.setPaymentApprovalIdempotency(payment);
            payment = paymentRepository.save(payment);

            PaymentHistory history = PaymentHistory.builder()
                    .payment(payment)
                    .previousStatus(null)
                    .currentStatus(PaymentStatus.APPROVED)
                    .changeReason("토스페이먼츠 결제 승인")
                    .rawResponse(objectMapper.writeValueAsString(tossResponse))
                    .build();

            idempotencyService.setPaymentCancelIdempotency(history);
            paymentHistoryRepository.save(history);

            log.info("결제 승인 완료: paymentId={}, paymentKey={}", payment.getId(), payment.getPaymentKey());
            return paymentConverter.toConfirmResponse(payment);

        } catch (Exception e) {
            log.error("결제 승인 실패: paymentKey={}, error={}", request.getPaymentKey(), e.getMessage());

            Payment failedPayment = Payment.builder()
                    .paymentKey(request.getPaymentKey())
                    .orderId(UUID.fromString(request.getOrderId()))
                    .userId(userId)
                    .amount(request.getAmount())
                    .paymentMethod("UNKNOWN")
                    .paymentStatus(PaymentStatus.FAILED)
                    .failedReason(e.getMessage())
                    .rawResponse("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();

            failedPayment = paymentRepository.save(failedPayment);

            PaymentHistory failedHistory = PaymentHistory.builder()
                    .payment(failedPayment)
                    .previousStatus(null)
                    .currentStatus(PaymentStatus.FAILED)
                    .changeReason("토스페이먼츠 결제 승인 실패: " + e.getMessage())
                    .rawResponse("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();

            paymentHistoryRepository.save(failedHistory);

            throw new PaymentException(PaymentErrorCode.PAYMENT_APPROVAL_FAILED);
        }
    }

    @Override
    public PaymentResponseDTO.PaymentCancelResponseDTO cancelPayment(PaymentRequestDTO.PaymentCancelRequestDTO request, UUID orderId, UUID userId) {
        log.info("결제 취소 시작: orderId={}, userId={}, reason={}", orderId, userId, request.getCancelReason());

        Payment payment = paymentRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.canCancel()) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        var existingCancel = idempotencyService.checkPaymentCancelIdempotency(payment.getId(), request.getCancelReason());
        if (existingCancel.isPresent()) {
            log.info("중복 결제 취소 요청 무시: paymentId={}, reason={}", payment.getId(), request.getCancelReason());
            PaymentHistory history = existingCancel.get();
            return paymentConverter.toCancelResponse(payment, history);
        }

        try {
            tossApiClient.cancelPayment(payment.getPaymentKey(), request.getCancelReason());

            payment.cancel(request.getCancelReason(), LocalDateTime.now(), "{\"cancelReason\":\"" + request.getCancelReason() + "\"}");
            payment = paymentRepository.save(payment);

            PaymentHistory history = PaymentHistory.builder()
                    .payment(payment)
                    .previousStatus(PaymentStatus.APPROVED)
                    .currentStatus(PaymentStatus.CANCELED)
                    .changeReason(request.getCancelReason())
                    .rawResponse("{\"cancelReason\":\"" + request.getCancelReason() + "\"}")
                    .build();

            idempotencyService.setPaymentCancelIdempotency(history);
            history = paymentHistoryRepository.save(history);

            log.info("결제 취소 완료: paymentId={}, paymentKey={}", payment.getId(), payment.getPaymentKey());
            return paymentConverter.toCancelResponse(payment, history);

        } catch (Exception e) {
            log.error("결제 취소 실패: paymentId={}, error={}", payment.getId(), e.getMessage());
            throw new PaymentException(PaymentErrorCode.PAYMENT_CANCEL_FAILED);
        }
    }

    @Override
    public void updateStatusFromWebhook(String payload) {
        log.info("웹훅 수신: payload={}", payload);
        
        try {
            if (!webhookSignatureService.verifySignature(payload)) {
                log.error("웹훅 서명 검증 실패");
                throw new PaymentException(PaymentErrorCode.INVALID_WEBHOOK_SIGNATURE);
            }

            TossWebhookPayload webhookPayload = objectMapper.readValue(payload, TossWebhookPayload.class);

            var existingWebhook = idempotencyService.checkWebhookIdempotency(
                webhookPayload.getPaymentKey(), webhookPayload.getStatus());
            if (existingWebhook.isPresent()) {
                log.info("중복 웹훅 무시: paymentKey={}, status={}", 
                    webhookPayload.getPaymentKey(), webhookPayload.getStatus());
                return;
            }

            Payment payment = paymentRepository.findByPaymentKey(webhookPayload.getPaymentKey())
                    .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

            PaymentStatus newStatus = convertTossStatus(webhookPayload.getStatus());
            if (payment.getPaymentStatus() == newStatus) {
                log.info("웹훅 상태가 현재 상태와 동일: paymentKey={}, status={}", 
                    webhookPayload.getPaymentKey(), newStatus);
                return;
            }

            PaymentStatus previousStatus = payment.getPaymentStatus();

            switch (newStatus) {
                case APPROVED:
                    payment.approve(LocalDateTime.now(), payload);
                    break;
                case FAILED:
                    payment.fail(webhookPayload.getFailure() != null ? webhookPayload.getFailure().getMessage() : "웹훅으로 인한 실패", payload);
                    break;
                case CANCELED:
                    payment.cancel("웹훅으로 인한 취소", LocalDateTime.now(), payload);
                    break;
                default:
                    log.warn("알 수 없는 웹훅 상태: {}", newStatus);
                    return;
            }

            payment = paymentRepository.save(payment);

            PaymentHistory history = PaymentHistory.builder()
                    .payment(payment)
                    .previousStatus(previousStatus)
                    .currentStatus(newStatus)
                    .changeReason("토스페이먼츠 웹훅 상태 변경: " + webhookPayload.getStatus())
                    .rawResponse(payload)
                    .build();

            idempotencyService.setWebhookIdempotency(history);
            paymentHistoryRepository.save(history);

            log.info("웹훅 상태 업데이트 완료: paymentKey={}, {} → {}", 
                webhookPayload.getPaymentKey(), previousStatus, newStatus);

        } catch (Exception e) {
            log.error("웹훅 처리 실패: error={}", e.getMessage());
            throw new PaymentException(PaymentErrorCode.WEBHOOK_PROCESSING_FAILED);
        }
    }


    private PaymentStatus convertTossStatus(String tossStatus) {
        return switch (tossStatus.toUpperCase()) {
            case "DONE", "APPROVED" -> PaymentStatus.APPROVED;
            case "CANCELED" -> PaymentStatus.CANCELED;
            case "FAILED" -> PaymentStatus.FAILED;
            default -> throw new PaymentException(PaymentErrorCode.UNKNOWN_TOSS_STATUS);
        };
    }
}
