package com.example.cloudfour.paymentservice.domain.payment.controller;

import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentRequestDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.service.command.PaymentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentRedirectController {
    private final PaymentCommandService paymentCommandService;


    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam int amount,
                                 Model model) {
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);

        PaymentRequestDTO.PaymentVerifyRequestDTO verifyRequest = PaymentRequestDTO.PaymentVerifyRequestDTO.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();

        try {
            PaymentResponseDTO.PaymentVerifyResponseDTO verified = paymentCommandService.verifyPayment(verifyRequest, null);

            model.addAttribute("method", verified.getPaymentMethod());
            model.addAttribute("status", verified.getPaymentStatus());

            return "payment/payment-success";

        } catch (Exception e) {
            model.addAttribute("code", "VERIFY_FAILED");
            model.addAttribute("message", e.getMessage());
            return "payment/payment-fail";
        }
    }

    @GetMapping("/fail")
    public String paymentFail(@RequestParam String code,
                              @RequestParam String message,
                              @RequestParam String orderId,
                              Model model) {

        model.addAttribute("code", code);
        model.addAttribute("message", message);
        model.addAttribute("orderId", orderId);

        paymentCommandService.recordPaymentFail(orderId, message);

        return "payment/payment-fail";
    }
}

