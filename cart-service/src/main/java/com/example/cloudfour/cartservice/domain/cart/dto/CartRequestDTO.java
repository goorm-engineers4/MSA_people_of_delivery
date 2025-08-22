package com.example.cloudfour.cartservice.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class CartRequestDTO {
    @Getter
    @Builder
    public static class CartCreateRequestDTO{
        UUID storeId;
        UUID menuId;
        List<UUID> menuOptionIds;
    }
}
