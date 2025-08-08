package com.example.cloudfour.cartservice.cartitem.repository;

import com.example.cloudfour.cartservice.cartitem.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    @Query("select count(ci) > 0  from CartItem ci join fetch Cart c on ci.cart.id = c.id where ci.id =:cartItemId and c.user =:userId and c.userIsDeleted = false")
    boolean existsByCartItemAndUser(@Param("cartItemId") UUID cartItemId, @Param("userId") UUID userId);

    @Query("select ci from CartItem ci join fetch Cart c on ci.cart.id = c.id where c.id =:cartId and c.user =:userId and c.userIsDeleted = false and c.storeIsDeleted = false")
    List<CartItem> findAllByCartId(@Param("cartId") UUID cartId,  @Param("userId") UUID userId);
}
