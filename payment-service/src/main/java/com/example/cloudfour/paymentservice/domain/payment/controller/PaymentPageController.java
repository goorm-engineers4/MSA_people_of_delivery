package com.example.cloudfour.paymentservice.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentPageController {

    @Value("${toss.client-key:test-key}")
    private String clientKey;

    @GetMapping("/payments/{orderId}/sdk-test")
    public String showPaymentPage(@PathVariable UUID orderId) {
        log.info("결제 페이지 요청: orderId={}", orderId);

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>결제 페이지</title>
                <meta charset="UTF-8">
            </head>
            <body>
                <h1>결제 페이지</h1>
                <p>주문 ID: %s</p>
                <p>클라이언트 키: %s</p>
                <p>결제 서비스가 준비 중입니다.</p>
            </body>
            </html>
            """, orderId, clientKey);
    }
}
