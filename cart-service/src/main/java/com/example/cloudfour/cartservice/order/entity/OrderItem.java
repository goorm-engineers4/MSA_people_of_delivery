package com.example.cloudfour.cartservice.order.entity;

import com.example.cloudfour.cartservice.order.exception.OrderItemErrorCode;
import com.example.cloudfour.cartservice.order.exception.OrderItemException;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_orderItem")
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;

    private Integer quantity;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @Column(name = "menuId", nullable = false)
    private UUID menu;

    @Column(name = "menuOptionId", nullable = false)
    private UUID menuOption;

    public static class OrderItemBuilder{
        private OrderItemBuilder id(UUID id){
            throw new OrderItemException(OrderItemErrorCode.CREATE_FAILED);
        }
    }

    public void setOrder(Order order){
        this.order = order;
        order.getOrderItems().add(this);
    }

    public void setMenu(UUID menu){
        this.menu = menu;
    }

    public void setMenuOption(UUID menuOption){
        if (menuOption != null) {
            this.menuOption = menuOption;
        }
    }
}
