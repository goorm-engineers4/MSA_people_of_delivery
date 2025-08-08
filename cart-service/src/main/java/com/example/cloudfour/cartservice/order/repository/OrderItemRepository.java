package com.example.cloudfour.cartservice.order.repository;

import com.example.cloudfour.cartservice.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    @Query("select oi from OrderItem oi where oi.order.id =:OrderId and oi.order.isDeleted = false")
    List<OrderItem> findByOrderId(@Param("OrderId") UUID orderId);
}
