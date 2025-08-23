package com.example.cloudfour.paymentservice.domain.payment.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryServiceImpl implements PaymentQueryService {

    @Override
    public String getDetailPayment(UUID orderId, UUID userId) {
        log.info("결제 상세 조회: orderId={}, userId={}", orderId, userId);
        return String.format("결제 상세 정보 - 주문ID: %s, 사용자ID: %s", orderId, userId);
    }

    @Override
    public String getUserListPayment(UUID userId) {
        log.info("사용자 결제 목록 조회: userId={}", userId);
        return String.format("사용자 결제 목록 - 사용자ID: %s", userId);
    }

    @Override
    public String getStoreListPayment(UUID storeId, UUID userId) {
        log.info("스토어 결제 목록 조회: storeId={}, userId={}", storeId, userId);
        return String.format("스토어 결제 목록 - 스토어ID: %s, 사용자ID: %s", storeId, userId);
    }

    @Override
    public String getStoreSummaryPayment(UUID storeId, UUID userId) {
        log.info("스토어 결제 요약 조회: storeId={}, userId={}", storeId, userId);
        return String.format("스토어 결제 요약 - 스토어ID: %s, 사용자ID: %s", storeId, userId);
    }
}
