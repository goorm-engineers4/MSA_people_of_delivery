package com.example.cloudfour.cartservice.domain.order.entity;

import com.example.cloudfour.cartservice.domain.order.exception.OrderItemErrorCode;
import com.example.cloudfour.cartservice.domain.order.exception.OrderItemException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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

    // 단일 옵션 (기존 호환성 유지)
    @Column(name = "menuOptionId")
    private UUID menuOption;

    // 여러 옵션을 위한 새로운 구조
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemOption> options = new ArrayList<>();

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

    public void addOption(OrderItemOption option) {
        option.setOrderItem(this);
        this.options.add(option);
    }

    public void addOptions(List<OrderItemOption> options) {
        if (options != null) {
            options.forEach(this::addOption);
        }
    }
}
