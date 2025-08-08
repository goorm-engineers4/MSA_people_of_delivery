package com.example.cloudfour.cartservice.cartitem.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class CartItemRequestDTO {
    @Getter
    @Builder
    public static class CartItemAddRequestDTO {
        private UUID menuId;
        private UUID menuOptionId;
        private Integer quantity;
        private Integer price;
    }

    @Getter
    @Builder
    public static class CartItemCreateRequestDTO {
        private UUID menuId;
        private UUID menuOptionId;
    }

    @Getter
    @Builder
    public static class CartItemUpdateRequestDTO {
        private UUID menuOptionId;
        private Integer quantity;
    }
}
