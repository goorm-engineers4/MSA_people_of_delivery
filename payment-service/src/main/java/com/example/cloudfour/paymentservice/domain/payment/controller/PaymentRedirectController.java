package com.example.cloudfour.paymentservice.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentRedirectController {

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam int amount) {
        log.info("결제 성공: paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>결제 성공</title>
                <meta charset="UTF-8">
            </head>
            <body>
                <h1>결제 성공!</h1>
                <p>결제 키: %s</p>
                <p>주문 ID: %s</p>
                <p>금액: %d원</p>
                <p>결제가 성공적으로 완료되었습니다.</p>
            </body>
            </html>
            """, paymentKey, orderId, amount);
    }

    @GetMapping("/fail")
    public String paymentFail(@RequestParam String code,
                              @RequestParam String message,
                              @RequestParam String orderId) {
        log.warn("결제 실패: code={}, message={}, orderId={}", code, message, orderId);
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>결제 실패</title>
                <meta charset="UTF-8">
            </head>
            <body>
                <h1>결제 실패</h1>
                <p>오류 코드: %s</p>
                <p>오류 메시지: %s</p>
                <p>주문 ID: %s</p>
                <p>결제에 실패했습니다. 다시 시도해주세요.</p>
            </body>
            </html>
            """, code, message, orderId);
    }
}

