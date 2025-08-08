package com.example.cloudfour.cartservice.order.controller;

import com.example.cloudfour.cartservice.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class OrderCommonResponseDTO {
    UUID orderId;
    OrderStatus orderStatus;
    Integer totalPrice;
    LocalDateTime createdAt;
}
