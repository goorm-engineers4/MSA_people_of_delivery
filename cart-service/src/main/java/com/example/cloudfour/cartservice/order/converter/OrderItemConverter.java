package com.example.cloudfour.cartservice.order.converter;


import com.example.cloudfour.cartservice.cartitem.entity.CartItem;
import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
import com.example.cloudfour.cartservice.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.cartservice.order.entity.Order;
import com.example.cloudfour.cartservice.order.entity.OrderItem;

public class OrderItemConverter {
    public static OrderItemResponseDTO.OrderItemListResponseDTO toOrderItemClassListDTO(OrderItem orderItem, MenuOptionResponseDTO option) {
        return OrderItemResponseDTO.OrderItemListResponseDTO.builder()
                .option(option)
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }

    public static OrderItem CartItemtoOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = OrderItem.builder()
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
        orderItem.setMenu(cartItem.getMenu());
        orderItem.setOrder(order);
        orderItem.setMenuOption(cartItem.getMenuOption());
        return orderItem;

    }
}