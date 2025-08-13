package com.example.cloudfour.cartservice.order.entity;

import com.example.cloudfour.cartservice.order.enums.OrderStatus;
import com.example.cloudfour.cartservice.order.enums.OrderType;
import com.example.cloudfour.cartservice.order.enums.ReceiptType;
import com.example.cloudfour.cartservice.order.exception.OrderErrorCode;
import com.example.cloudfour.cartservice.order.exception.OrderException;
import com.example.cloudfour.modulecommon.entity.BaseEntity;
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
@Table(name = "p_order")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptType receiptType;

    @Column(nullable = false)
    private String address;

    private String request;

    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    @Builder.Default
    private boolean userIsDeleted = false;

    @Column(name = "userId", nullable = false)
    private UUID user;

    @Column(name = "storeId", nullable = false)
    private UUID store;

    @Column(name = "paymentId", nullable = false)
    private UUID payment;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public static class OrderBuilder{
        private OrderBuilder id(UUID id) {
            throw new OrderException(OrderErrorCode.CREATE_FAILED);
        }
    }

    public void setUser(UUID user){
        this.user = user;
    }

    public void setStore(UUID store){
        this.store = store;
    }

    public void setPayment(UUID payment){
        this.payment = payment;
    }

    public void updateOrderStatus(OrderStatus orderStatus){
        this.status = orderStatus;
    }
}
