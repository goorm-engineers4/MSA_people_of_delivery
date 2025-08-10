package com.example.cloudfour.cartservice.cartitem.controller;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class CartItemCommonResponseDTO {
    UUID cartItemId;
    UUID cartId;
    UUID menuOptionId;
    Integer quantity;
    Integer price;
}
