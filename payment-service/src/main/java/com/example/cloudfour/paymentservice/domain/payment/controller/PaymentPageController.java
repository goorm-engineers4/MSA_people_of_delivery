package com.example.cloudfour.paymentservice.domain.payment.controller;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.order.repository.OrderRepository;
import com.example.cloudfour.peopleofdelivery.global.auth.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentPageController {

    @Value("${toss.client-key}")
    private String clientKey;
    private final OrderRepository orderRepository;

    @GetMapping("/payments/{orderId}/sdk-test")
    public String showPaymentPage(@PathVariable UUID orderId,
                                  @AuthenticationPrincipal CustomUserDetails user,
                                  Model model) throws AccessDeniedException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인의 주문이 아닙니다.");
        }

        model.addAttribute("orderId", order.getId().toString());
        model.addAttribute("amount", order.getTotalPrice());
        model.addAttribute("clientKey", clientKey);

        return "payment/payment";
    }
}
