package com.example.cloudfour.storeservice.domain.menu.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_menucategory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuCategory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "menuCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    public static class MenuCategoryBuilder{
        private MenuCategoryBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }
}