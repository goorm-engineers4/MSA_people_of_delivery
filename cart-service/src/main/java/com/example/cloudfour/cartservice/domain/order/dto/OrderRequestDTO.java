package com.example.cloudfour.cartservice.domain.order.dto;

import com.example.cloudfour.cartservice.domain.order.enums.OrderStatus;
import com.example.cloudfour.cartservice.domain.order.enums.OrderType;
import com.example.cloudfour.cartservice.domain.order.enums.ReceiptType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class OrderRequestDTO {
    @Getter
    @Builder
    public static class OrderCreateRequestDTO {
        OrderType orderType;
        OrderStatus orderStatus;
        ReceiptType receiptType;
        String request;
    }

    @Getter
    @Builder
    public static class OrderUpdateRequestDTO {
        OrderStatus newStatus;
    }
}
