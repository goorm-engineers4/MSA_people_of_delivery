package com.example.cloudfour.cartservice.domain.cartitem.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class CartItemRequestDTO {
    @Getter
    @Builder
    public static class CartItemCreateRequestDTO {
        private UUID menuId;
        private List<UUID> menuOptionIds;
        private Integer quantity;
        private Integer price;
    }

    @Getter
    @Builder
    public static class CartItemAddRequestDTO {
        private UUID menuId;
        private List<UUID> menuOptionIds;
    }

    @Getter
    @Builder
    public static class CartItemUpdateRequestDTO {
        private List<UUID> menuOptionIds;
        private Integer quantity;
    }
}
