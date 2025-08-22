package com.example.cloudfour.paymentservice.domain.payment.apiclient;

import com.example.cloudfour.paymentservice.domain.payment.dto.TossApproveResponse;
import com.example.cloudfour.paymentservice.domain.payment.dto.TossCancelResponse;
import com.example.cloudfour.paymentservice.domain.payment.exception.PaymentErrorCode;
import com.example.cloudfour.paymentservice.domain.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossApiClient {

    private final RestClient restClient;

    @Value("${toss.secret-key}")
    private String secretKey;

    public TossApproveResponse approvePayment(String paymentKey, String orderId, int amount, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key는 null일 수 없습니다.");
        }

        return restClient.post()
            .uri("/v1/payments/confirm")
            .headers(h -> {
                h.setBasicAuth(secretKey, "");
                h.set("Idempotency-Key", idempotencyKey);})
            .body(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new PaymentException(PaymentErrorCode.TOSS_API_ERROR,
                    "토스 결제 승인 API 호출에 실패했습니다. status: " + response.getStatusCode());
            })
            .body(TossApproveResponse.class);
    }

    public TossCancelResponse cancelPayment(String paymentKey, String cancelReason) {
        return restClient.post()
            .uri("/v1/payments/" + paymentKey + "/cancel")
            .headers(h -> h.setBasicAuth(secretKey, ""))
            .body(Map.of("cancelReason", cancelReason))
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                throw new PaymentException(PaymentErrorCode.TOSS_API_ERROR,
                    "토스 결제 취소 API 호출에 실패했습니다. status: " + response.getStatusCode());
            })
            .body(TossCancelResponse.class);
    }
}
