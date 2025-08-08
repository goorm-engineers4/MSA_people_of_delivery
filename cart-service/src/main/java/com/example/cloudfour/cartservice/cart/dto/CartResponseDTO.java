package com.example.cloudfour.cartservice.cart.dto;

import com.example.cloudfour.cartservice.cart.controller.CartCommonResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class CartResponseDTO {
    @Getter
    @Builder
    public static class CartCreateResponseDTO{
        CartCommonResponseDTO cartCommonResponseDTO;
        UUID cartItemId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class CartDetailResponseDTO {
        CartCommonResponseDTO cartCommonResponseDTO;
    }
}