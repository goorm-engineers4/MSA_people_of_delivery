package com.example.cloudfour.storeservice.domain.menu.entity;


import com.example.cloudfour.storeservice.domain.menu.enums.MenuStatus;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuException;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Menu {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "menuPicture", columnDefinition = "TEXT")
    private String menuPicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MenuStatus status;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuCategoryId", nullable = false)
    private MenuCategory menuCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "menu")
    @Builder.Default
    private List<MenuOption> menuOptions = new ArrayList<>();

    public static class MenuBuilder {
        private MenuBuilder id(UUID id) {
            throw new MenuException(MenuErrorCode.CREATE_FAILED);
        }
    }

    public void setMenuCategory(MenuCategory menuCategory){
        this.menuCategory = menuCategory;
        menuCategory.getMenus().add(this);
    }

    public void setStore(Store store){
        this.store = store;
        store.getMenus().add(this);
    }

    public void updateMenuInfo(String name, String content, Integer price, String menuPicture, MenuStatus status) {
        if (name != null) this.name = name;
        if (content != null) this.content = content;
        if (price != null) this.price = price;
        if (menuPicture != null) this.menuPicture = menuPicture;
        if (status != null) this.status = status;
    }
}