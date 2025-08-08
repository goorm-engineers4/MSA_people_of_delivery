package com.example.cloudfour.storeservice.domain.store.entity;


import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import com.example.cloudfour.storeservice.domain.region.entity.Region;
import com.example.common.entity.BaseEntity; //추후 common/build.gradle에 의존성 추가할 예정
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
@Table(name = "p_store")
public class Store extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    private String storePicture;

    @Column(nullable = false, length = 255)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer minPrice;

    @Column(nullable = false)
    private Integer deliveryTip;

    private Float rating;

    private Integer likeCount;

    private Integer reviewCount;

    @Column(nullable = false, length = 255)
    private String operationHours;

    @Column(nullable = false, length = 255)
    private String closedDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeCategoryId", nullable = false)
    private StoreCategory storeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regionId", nullable = false)
    private Region region;

    /** 소유자 ID (User 마이크로서비스 참조 대신 UUID로만 저장) */
    @Column(nullable = false, name = "owner_id")
    private UUID ownerId;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "store", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    public void setStoreCategory(StoreCategory storeCategory) {
        this.storeCategory = storeCategory;
        storeCategory.getStores().add(this);
    }

    public void setRegion(Region region) {
        this.region = region;
        region.getStores().add(this);
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void update(String name, String address) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
    }

    /** 소프트 삭제 */
    public void softDelete() {
        this.setDeleted(true); // BaseEntity에 deleted 플래그가 있다고 가정
    }
}
