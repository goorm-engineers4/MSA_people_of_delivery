package com.example.cloudfour.cartservice.order.dto;

import com.example.cloudfour.cartservice.order.controller.OrderCommonResponseDTO;
import com.example.cloudfour.cartservice.order.enums.OrderStatus;
import com.example.cloudfour.cartservice.order.enums.OrderType;
import com.example.cloudfour.cartservice.order.enums.ReceiptType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponseDTO {
    @Getter
    @Builder
    public static class OrderCreateResponseDTO {
        OrderCommonResponseDTO orderCommonResponseDTO;
    }

    @Getter
    @Builder
    public static class OrderDetailResponseDTO {
        String storeName;
        OrderType orderType;
        ReceiptType receiptType;
        String address;
        String request;
        OrderCommonResponseDTO orderCommonResponseDTO;
        List<OrderItemResponseDTO.OrderItemListResponseDTO> items;
    }

    @Getter
    @Builder
    public static class OrderUserResponseDTO {
        String storeName;
        OrderCommonResponseDTO orderCommonResponseDTO;
    }

    @Getter
    @Builder
    public static class OrderUserListResponseDTO {
        List<OrderUserResponseDTO>  orderUsers;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class OrderStoreResponseDTO {
        String userName;
        OrderType orderType;
        ReceiptType receiptType;
        OrderCommonResponseDTO orderCommonResponseDTO;
    }

    @Getter
    @Builder
    public static class OrderStoreListResponseDTO {
        List<OrderStoreResponseDTO> orderStores;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class OrderUpdateResponseDTO {
        UUID orderId;
        OrderStatus previousStatus;
        OrderStatus currentStatus;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class OrderStatusResponseDTO {
        OrderStatus orderStatus;
        LocalDateTime updatedAt;
        UUID updatedBy;
    }
}
