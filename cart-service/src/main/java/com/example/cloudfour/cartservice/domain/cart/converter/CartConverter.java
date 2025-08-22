package com.example.cloudfour.cartservice.domain.cart.converter;

import com.example.cloudfour.cartservice.domain.cart.controller.CartCommonResponseDTO;
import com.example.cloudfour.cartservice.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.cartservice.domain.cart.entity.Cart;
import com.example.cloudfour.cartservice.domain.cartitem.converter.CartItemConverter;

import java.util.UUID;

public class CartConverter {
    public static CartResponseDTO.CartDetailResponseDTO toCartDetailResponseDTO(Cart cart) {
        return CartResponseDTO.CartDetailResponseDTO.builder()
                .cartCommonResponseDTO(toCartCommonResponseDTO(cart))
                .cartItems(cart.getCartItems().stream()
                        .map(CartItemConverter::toCartItemListResponseDTO)
                        .toList())
                .build();
    }

    public static CartResponseDTO.CartCreateResponseDTO toCartCreateResponseDTO(Cart cart, UUID cartItemId) {
        return CartResponseDTO.CartCreateResponseDTO.builder()
                .cartCommonResponseDTO(toCartCommonResponseDTO(cart))
                .cartItemId(cartItemId)
                .createdAt(cart.getCreatedAt())
                .build();
    }

    public static CartCommonResponseDTO toCartCommonResponseDTO(Cart cart){
        return CartCommonResponseDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUser())
                .storeId(cart.getStore())
                .build();
    }
}
