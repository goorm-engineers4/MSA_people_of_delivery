package com.example.cloudfour.cartservice.domain.order.converter;

import com.example.cloudfour.cartservice.domain.cartitem.entity.CartItem;
import com.example.cloudfour.cartservice.domain.cartitem.entity.CartItemOption;
import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
import com.example.cloudfour.cartservice.domain.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.cartservice.domain.order.entity.Order;
import com.example.cloudfour.cartservice.domain.order.entity.OrderItem;
import com.example.cloudfour.cartservice.domain.order.entity.OrderItemOption;

import java.util.List;
import java.util.stream.Collectors;

public class OrderItemConverter {
    
    public static OrderItemResponseDTO.OrderItemListResponseDTO toOrderItemClassListDTO(OrderItem orderItem, MenuOptionResponseDTO option) {
        return OrderItemResponseDTO.OrderItemListResponseDTO.builder()
                .option(option)
                .build();
    }

    public static OrderItemResponseDTO.OrderItemListResponseDTO toOrderItemClassListDTO(OrderItem orderItem) {
        List<OrderItemResponseDTO.OrderItemOptionDTO> options = orderItem.getOptions().stream()
                .map(option -> OrderItemResponseDTO.OrderItemOptionDTO.builder()
                        .menuOptionId(option.getMenuOptionId())
                        .additionalPrice(option.getAdditionalPrice())
                        .optionName(option.getOptionName())
                        .build())
                .collect(Collectors.toList());

        return OrderItemResponseDTO.OrderItemListResponseDTO.builder()
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .menuId(orderItem.getMenu())
                .options(options)
                .build();
    }

    public static OrderItem CartItemtoOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = OrderItem.builder()
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
        
        orderItem.setMenu(cartItem.getMenu());
        orderItem.setOrder(order);

        if (cartItem.getOptions() != null && !cartItem.getOptions().isEmpty()) {
            CartItemOption firstOption = cartItem.getOptions().get(0);
            orderItem.setMenuOption(firstOption.getMenuOptionId());
        }

        if (cartItem.getOptions() != null && !cartItem.getOptions().isEmpty()) {
            List<OrderItemOption> orderItemOptions = cartItem.getOptions().stream()
                    .map(cartOption -> OrderItemOption.builder()
                            .menuOptionId(cartOption.getMenuOptionId())
                            .additionalPrice(cartOption.getAdditionalPrice())
                            .optionName(cartOption.getOptionName())
                            .build())
                    .collect(Collectors.toList());
            
            orderItem.addOptions(orderItemOptions);
        }
        
        return orderItem;
    }
}