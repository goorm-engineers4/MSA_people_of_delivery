package com.example.cloudfour.cartservice.cart.entity;

import com.example.cloudfour.cartservice.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.cart.exception.CartException;
import com.example.cloudfour.cartservice.cartitem.entity.CartItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name="p_cart")
public class Cart {
    @Id
    @GeneratedValue
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "userId" ,nullable = false)
    private UUID user;

    @Column(nullable = false)
    private boolean userIsDeleted = false;

    @Column(nullable = false)
    private boolean storeIsDeleted = false;

    @Column(name = "storeId" ,nullable = false)
    private UUID store;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public static class CartBuilder{
        private CartBuilder id(UUID id){
            throw new CartException(CartErrorCode.CREATE_FAILED);
        }
    }

    public void setUser(UUID user){
        this.user = user;
    }

    public void setStore(UUID store){
        this.store = store;
    }

    public void setUserIsDeleted(){
        this.userIsDeleted = true;
    }

    public void setStoreIsDeleted(){
        this.storeIsDeleted = true;
    }
}
