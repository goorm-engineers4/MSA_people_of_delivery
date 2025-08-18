package com.example.cloudfour.storeservice.domain.collection.document;

import com.example.cloudfour.storeservice.domain.menu.enums.MenuStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document("store-service")
@Table(name = "store_document")
public class StoreDocument {
    @Id
    private UUID id;

    private UUID storeId;
    private UUID storeCategoryId;
    private UUID userId;

    private String name;
    private String address;
    private String phone;
    private String content;
    private Integer minPrice;
    private Integer deliveryTip;
    private Float rating;
    private Integer likeCount;
    private Integer reviewCount;
    private String OperationHours;
    private String closedDays;
    private String storeCategory;
    private String siDo;
    private String siGunGu;
    private String eupMyeonDong;
    private String pictureURL;

    private LocalDateTime createdAt;
    @Transient
    private List<StoreDocument.Menu> menus;
    @Transient
    private List<StoreDocument.Review> reviews;

    @Getter
    @Builder
    public static class Menu {
        private UUID id;
        private UUID menuCategoryId;
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private String menuCategory;
        private MenuStatus menuStatus;
        private List<StoreDocument.MenuOption> menuOptions;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class Review {
        private UUID id;
        private Double score;
        private String content;
    }

    @Getter
    @Builder
    public static class MenuOption {
        private UUID id;
        private Integer additionalPrice;
        private String optionName;
    }
}
