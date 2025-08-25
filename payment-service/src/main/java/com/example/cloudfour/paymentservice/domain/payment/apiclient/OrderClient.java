package com.example.cloudfour.paymentservice.domain.payment.apiclient;

import com.example.cloudfour.paymentservice.commondto.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClient {

    private final RestTemplate restTemplate;

    private static final String BASE = "http://cart-service/internal";

    public OrderResponseDTO getOrderById(String orderId, UUID userId) {
        try {
            String url = BASE + "/orders/" + orderId + "?userId=" + userId;
            log.info("주문 정보 조회 요청: url={}", url);
            
            OrderResponseDTO order = restTemplate.getForObject(url, OrderResponseDTO.class);
            log.info("주문 정보 조회 성공: orderId={}, userId={}", orderId, userId);
            
            return order;
        } catch (Exception e) {
            log.error("주문 정보 조회 실패: orderId={}, userId={}, error={}", orderId, userId, e.getMessage());
            throw new RuntimeException("주문 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public boolean validateOrder(String orderId, UUID userId, Integer amount) {
        try {
            OrderResponseDTO order = getOrderById(orderId, userId);
            
            if (order == null) {
                log.warn("주문 정보가 존재하지 않음: orderId={}, userId={}", orderId, userId);
                return false;
            }
            
            if (!userId.equals(order.getUserId())) {
                log.warn("주문 소유자가 일치하지 않음: orderId={}, expectedUserId={}, actualUserId={}", 
                    orderId, userId, order.getUserId());
                return false;
            }
            
            if (!amount.equals(order.getTotalPrice())) {
                log.warn("주문 금액이 일치하지 않음: orderId={}, expectedAmount={}, actualAmount={}", 
                    orderId, amount, order.getTotalPrice());
                return false;
            }
            
            log.info("주문 검증 성공: orderId={}, userId={}, amount={}", orderId, userId, amount);
            return true;
            
        } catch (Exception e) {
            log.error("주문 검증 실패: orderId={}, userId={}, amount={}, error={}", 
                orderId, userId, amount, e.getMessage());
            return false;
        }
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        try {
            String url = BASE + "/orders/" + orderId + "/status";
            log.info("주문 상태 업데이트 요청: url={}, newStatus={}", url, newStatus);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"newStatus\":\"%s\"}", newStatus);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            
            restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Void.class);
            log.info("주문 상태 업데이트 성공: orderId={}, newStatus={}", orderId, newStatus);
            
        } catch (Exception e) {
            log.error("주문 상태 업데이트 실패: orderId={}, newStatus={}, error={}", orderId, newStatus, e.getMessage());
            log.warn("주문 상태 업데이트 실패했지만 결제 처리는 계속 진행합니다.");
        }
    }
}

