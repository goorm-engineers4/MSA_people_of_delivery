package com.example.cloudfour.cartservice.order.dto;

import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

public class OrderItemResponseDTO {
    @Getter
    @Builder
    public static class OrderItemListResponseDTO{
        Integer quantity;
        Integer price;
        @JsonUnwrapped
        MenuOptionResponseDTO option;
    }
}