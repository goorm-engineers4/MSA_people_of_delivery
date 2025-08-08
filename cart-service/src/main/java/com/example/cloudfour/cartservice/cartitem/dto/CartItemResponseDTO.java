package com.example.cloudfour.cartservice.cartitem.dto;

import com.example.cloudfour.cartservice.cartitem.controller.CartItemCommonResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class CartItemResponseDTO {
    @Getter
    @Builder
    public static class CartItemAddResponseDTO {
        CartItemCommonResponseDTO cartItemCommonResponseDTO;
        UUID menuId;
    }

    @Getter
    @Builder
    public static class CartItemListResponseDTO {
        CartItemCommonResponseDTO cartItemCommonResponseDTO;
        UUID menuId;
    }

    @Getter
    @Builder
    public static class CartItemUpdateResponseDTO {
        CartItemCommonResponseDTO cartItemCommonResponseDTO;
    }
}
