package com.example.cloudfour.storeservice.domain.menu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_menuoption")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuOption {
    @Id
    @UuidGenerator
    @Column(name = "menuOptionId")
    private UUID id;

    @Column(name = "optionName", nullable = false)
    private String optionName;

    @Column(name = "additionalPrice", nullable = false)
    private Integer additionalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menuId", nullable = false)
    private Menu menu;


    public static class MenuOptionBuilder{
        private MenuOptionBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }

    public void setMenu(Menu menu){
        this.menu = menu;
        menu.getMenuOptions().add(this);
    }

    public void updateOptionInfo(String optionName, Integer additionalPrice) {
        if (optionName != null) this.optionName = optionName;
        if (additionalPrice != null) this.additionalPrice = additionalPrice;
    }
}