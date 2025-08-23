package com.example.cloudfour.paymentservice.domain.payment.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl implements PaymentCommandService {

    @Override
    @Transactional
    public String createPayment(String request, UUID userId) {
        log.info("결제 생성 요청: request={}, userId={}", request, userId);
        return String.format("결제 생성 완료 - 요청: %s, 사용자ID: %s", request, userId);
    }

    @Override
    @Transactional
    public String verifyPayment(String request, UUID userId) {
        log.info("결제 검증 요청: request={}, userId={}", request, userId);
        return String.format("결제 검증 완료 - 요청: %s, 사용자ID: %s", request, userId);
    }

    @Override
    @Transactional
    public String updatePayment(String request, UUID orderId, UUID userId) {
        log.info("결제 수정 요청: request={}, orderId={}, userId={}", request, orderId, userId);
        return String.format("결제 수정 완료 - 요청: %s, 주문ID: %s, 사용자ID: %s", request, orderId, userId);
    }

    @Override
    @Transactional
    public String cancelPayment(String request, UUID orderId, UUID userId) {
        log.info("결제 취소 요청: request={}, orderId={}, userId={}", request, orderId, userId);
        return String.format("결제 취소 완료 - 요청: %s, 주문ID: %s, 사용자ID: %s", request, orderId, userId);
    }

    @Override
    @Transactional
    public void updateStatusFromWebhook(String payload) {
        log.info("웹훅 상태 업데이트: payload={}", payload);
    }

    @Override
    @Transactional
    public void recordPaymentFail(String orderId, String message) {
        log.warn("결제 실패 기록: orderId={}, message={}", orderId, message);
    }
}
