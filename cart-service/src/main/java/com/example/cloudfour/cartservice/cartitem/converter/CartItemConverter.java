package com.example.cloudfour.cartservice.cartitem.converter;

import com.example.cloudfour.cartservice.cart.dto.CartRequestDTO;
import com.example.cloudfour.cartservice.cartitem.controller.CartItemCommonResponseDTO;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.cartitem.entity.CartItem;

public class CartItemConverter {

    public static CartItemResponseDTO.CartItemAddResponseDTO toCartItemAddResponseDTO(CartItem cartItem) {
        return CartItemResponseDTO.CartItemAddResponseDTO.builder()
                .cartItemCommonResponseDTO(toCartItemCommonResponseDTO(cartItem))
                .build();
    }

    public static CartItemResponseDTO.CartItemUpdateResponseDTO toCartItemUpdateResponseDTO(CartItem cartItem) {
        return CartItemResponseDTO.CartItemUpdateResponseDTO.builder()
                .cartItemCommonResponseDTO(toCartItemCommonResponseDTO(cartItem))
                .build();
    }

    public static CartItemResponseDTO.CartItemListResponseDTO toCartItemListResponseDTO(CartItem cartItem) {
        return CartItemResponseDTO.CartItemListResponseDTO.builder()
                .menuId(cartItem.getMenu())
                .cartItemCommonResponseDTO(toCartItemCommonResponseDTO(cartItem))
                .build();
    }

    public static CartItemRequestDTO.CartItemAddRequestDTO toCartItemAddRequestDTO(CartRequestDTO.CartCreateRequestDTO cartCreateRequestDTO, int price){
        return   CartItemRequestDTO.CartItemAddRequestDTO.builder()
                .menuId(cartCreateRequestDTO.getMenuId())
                .menuOptionId(cartCreateRequestDTO.getMenuOptionId())
                .quantity(1)
                .price(price)
                .build();
    }

    public static CartItemCommonResponseDTO toCartItemCommonResponseDTO(CartItem cartItem){
        return CartItemCommonResponseDTO.builder()
                .cartItemId(cartItem.getId())
                .cartId(cartItem.getCart().getId())
                .menuOptionId(cartItem.getMenuOption())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }
}