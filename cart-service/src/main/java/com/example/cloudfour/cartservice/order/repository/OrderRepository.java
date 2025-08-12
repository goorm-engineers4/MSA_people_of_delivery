package com.example.cloudfour.cartservice.order.repository;

import com.example.cloudfour.cartservice.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("select o from Order o where o.id =:OrderId and o.isDeleted = false")
    Optional<Order> findById(@Param("OrderId") UUID orderId);

    @Query("select o from Order o where o.user=:userId and o.userIsDeleted= false and o.isDeleted = false and o.createdAt <:cursor order by o.createdAt desc")
    Slice<Order> findAllByUserId(@Param("userId")  UUID userId, LocalDateTime cursor, Pageable pageable);

    @Query("select o from Order o where o.store=:StoreId and o.userIsDeleted = false and o.userIsDeleted = false and o.isDeleted = false and o.createdAt <:cursor order by o.createdAt desc")
    Slice<Order> findAllByStoreId(@Param("StoreId")  UUID storeId, LocalDateTime cursor, Pageable pageable);

    @Query("select count(o) > 0 from Order o where o.id =:OrderId and o.user =:UserId and o.userIsDeleted = false and o.isDeleted = false")
    boolean existsByOrderIdAndUserId(@Param("OrderId") UUID orderId, @Param("UserId") UUID userId);

    void deleteAllByCreatedAtBefore(LocalDateTime createdAtBefore);
}
