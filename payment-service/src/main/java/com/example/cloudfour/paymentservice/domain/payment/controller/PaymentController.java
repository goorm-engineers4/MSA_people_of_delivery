package com.example.cloudfour.paymentservice.domain.payment.controller;

import com.example.cloudfour.paymentservice.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.paymentservice.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.paymentservice.domain.payment.dto.TossWebhookPayload;
import com.example.cloudfour.paymentservice.domain.payment.service.command.PaymentCommandService;
import com.example.cloudfour.paymentservice.domain.payment.service.query.PaymentQueryService;
import com.example.cloudfour.modulecommon.apiPayLoad.CustomResponse;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name="Payment", description = "결제 API by 김준형")
public class PaymentController {
    private final PaymentQueryService paymentQueryService;
    private final PaymentCommandService paymentCommandService;

    @PostMapping("")
    @Operation(summary = "결제 생성", description = "결제를 생성합니다. 결제 생성에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentCreateResponseDTO> createPayment(
            @RequestBody PaymentRequestDTO.PaymentCreateRequestDTO paymentCreateRequestDTO,
            @AuthenticationPrincipal CurrentUser user
    ){
        PaymentResponseDTO.PaymentCreateResponseDTO payment = paymentCommandService.createPayment(paymentCreateRequestDTO, user.id());
        return CustomResponse.onSuccess(HttpStatus.CREATED, payment);
    }

    @PostMapping("/verify")
    @Operation(summary = "결제 검증", description = "결제를 검증합니다. 결제 검증에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentVerifyResponseDTO> verifyPayment(
            @RequestBody PaymentRequestDTO.PaymentVerifyRequestDTO paymentVerifyRequestDTO,
            @AuthenticationPrincipal CurrentUser user
    ){
        PaymentResponseDTO.PaymentVerifyResponseDTO payment = paymentCommandService.verifyPayment(paymentVerifyRequestDTO, user.id());
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @PostMapping("/webhook")
    @Operation(summary = "토스 결제 웹훅", description = "토스 결제 상태 변경에 대한 웹훅을 처리합니다.")
    public CustomResponse<Void> receiveWebhook(@RequestBody TossWebhookPayload payload) {
        paymentCommandService.updateStatusFromWebhook(payload);
        return CustomResponse.onSuccess(HttpStatus.OK, null);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "결제 수정", description = "결제를 수정합니다. 결제 수정에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentUpdateResponseDTO> updatePayment(
            @RequestBody PaymentRequestDTO.PaymentUpdateRequestDTO paymentUpdateRequestDTO,
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CurrentUser user
    ){
        PaymentResponseDTO.PaymentUpdateResponseDTO payment = paymentCommandService.updatePayment(paymentUpdateRequestDTO, orderId, user.id());
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @PatchMapping("/{orderId}/canceled")
    @Operation(summary = "결제 취소", description = "결제를 취소합니다. 결제 취소에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentCancelResponseDTO> cancelPayment(
            @RequestBody PaymentRequestDTO.PaymentCancelRequestDTO paymentCancelRequestDTO,
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CurrentUser user
    ){
        PaymentResponseDTO.PaymentCancelResponseDTO payment = paymentCommandService.cancelPayment(paymentCancelRequestDTO, orderId, user.id());
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "결제 상세 조회", description = "결제를 상세 조회합니다. 결제 상세 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentDetailResponseDTO> getPayment(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CurrentUser user
    ){
        PaymentResponseDTO.PaymentDetailResponseDTO payment = paymentQueryService.getDetailPayment(orderId, user.id());
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("store/{storeId}")
    @Operation(summary = "가게 결제 이력 조회", description = " 가게 결제 이력을 조회합니다. 가게 결제 이력 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentStoreListResponseDTO> getStorePayment(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal CurrentUser user
    ){
        PaymentResponseDTO.PaymentStoreListResponseDTO payment = paymentQueryService.getStoreListPayment(storeId, user.id());
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("/me")
    @Operation(summary = "내 결제 이력 조회", description = "내 결제 이력을 조회합니다. 내 결제 이력 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentUserListResponseDTO> getUserPayment(
            @AuthenticationPrincipal CurrentUser user
    ){
        PaymentResponseDTO.PaymentUserListResponseDTO payment = paymentQueryService.getUserListPayment(user.id());
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

    @GetMapping("store/{storeId}/summary")
    @Operation(summary = "가게 매출 요약", description = "가게 매출 요약을 조회합니다. 가게 매출 요약 조회에 사용되는 API입니다.")
    public CustomResponse<PaymentResponseDTO.PaymentStoreSummaryResponseDTO> getStoreSummaryPayment(
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal CurrentUser user
    ) {
        PaymentResponseDTO.PaymentStoreSummaryResponseDTO payment = paymentQueryService.getStoreSummaryPayment(storeId, user.id());
        return CustomResponse.onSuccess(HttpStatus.OK, payment);
    }

}
