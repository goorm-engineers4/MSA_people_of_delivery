package com.example.cloudfour.paymentservice.domain.payment.controller;

import com.example.cloudfour.modulecommon.apiPayLoad.CustomResponse;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name="Payment", description = "결제 API")
public class PaymentController {
    
    @GetMapping("/{orderId}")
    @Operation(summary = "결제 상세 조회", description = "결제를 상세 조회합니다.")
    public CustomResponse<String> getPayment(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CurrentUser user
    ){
        log.info("결제 상세 조회 요청: orderId={}, userId={}", orderId, user.id());
        String payment = "결제 상세 정보 - 주문ID: " + orderId + ", 사용자ID: " + user.id();
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("store/{storeId}")
    @Operation(summary = "가게 결제 이력 조회", description = "가게 결제 이력을 조회합니다.")
    public CustomResponse<String> getStorePayment(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal CurrentUser user
    ){
        log.info("가게 결제 이력 조회 요청: storeId={}, userId={}", storeId, user.id());
        String payment = "가게 결제 이력 - 스토어ID: " + storeId + ", 사용자ID: " + user.id();
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("/me")
    @Operation(summary = "내 결제 이력 조회", description = "내 결제 이력을 조회합니다.")
    public CustomResponse<String> getUserPayment(
            @AuthenticationPrincipal CurrentUser user
    ){
        log.info("내 결제 이력 조회 요청: userId={}", user.id());
        String payment = "내 결제 이력 - 사용자ID: " + user.id();
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("store/{storeId}/summary")
    @Operation(summary = "가게 매출 요약", description = "가게 매출 요약을 조회합니다.")
    public CustomResponse<String> getStoreSummaryPayment(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal CurrentUser user
    ) {
        log.info("가게 매출 요약 조회 요청: storeId={}, userId={}", storeId, user.id());
        String payment = "가게 매출 요약 - 스토어ID: " + storeId + ", 사용자ID: " + user.id();
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }
}
