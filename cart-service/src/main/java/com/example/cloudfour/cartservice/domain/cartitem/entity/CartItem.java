package com.example.cloudfour.cartservice.domain.cartitem.entity;

import com.example.cloudfour.cartservice.domain.cart.entity.Cart;
import com.example.cloudfour.cartservice.domain.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.cartservice.domain.cartitem.exception.CartItemException;
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
@Table(name = "p_cartitem")
public class CartItem {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId" ,nullable = false)
    private Cart cart;

    @Column(name = "menuId" ,nullable = false)
    private UUID menu;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItemOption> options = new ArrayList<>();

    public static class CartItemBuilder{
        private CartItemBuilder id(UUID id){
            throw new CartItemException(CartItemErrorCode.CREATE_FAILED);
        }
    }

    public void setCart(Cart cart){
        this.cart = cart;
        cart.getCartItems().add(this);
    }

    public void setMenu(UUID menu){
        this.menu = menu;
    }

    public void setMenuOption(UUID menuOptionId) {
        CartItemOption option = CartItemOption.builder()
                .menuOptionId(menuOptionId)
                .additionalPrice(0)
                .optionName("")
                .build();
        addOption(option);
    }

    public void addOption(CartItemOption option) {
        if (option != null) {
            option.setCartItem(this);
            this.options.add(option);
        }
    }

    public void update(Integer quantity, Integer price){
        if (quantity != null) this.quantity = quantity;
        if(price != null) this.price = price;
    }

}
