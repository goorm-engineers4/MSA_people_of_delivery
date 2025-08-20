package com.example.cloudfour.storeservice.domain.store.dto;

import com.example.cloudfour.storeservice.domain.store.controller.StoreCommonRequestDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class StoreRequestDTO {

    @Getter
    @Builder
    public static class StoreCreateRequestDTO {
        @JsonUnwrapped
        StoreCommonRequestDTO storeCommonRequestDTO;
        private String storePicture;
        private String phone;
        private String content;
        private Integer minPrice;
        private Integer deliveryTip;
        private String operationHours;
        private String closedDays;
    }

    @Getter
    @Builder
    public static class StoreUpdateRequestDTO {
        @JsonUnwrapped
        StoreCommonRequestDTO storeCommonRequestDTO;
    }

    @Getter
    @Builder
    public static class testRequestDTO{
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
        private String operationHours;
        private String closedDays;
        private String storeCategory;
        private String siDo;
        private String siGunGu;
        private String eupMyeonDong;
        private String pictureURL;

        private LocalDateTime createdAt;
        private List<MenuDto> menus;
        private List<ReviewDto> reviews;

        @Getter
        @Builder
        public static class MenuDto {
            private UUID id;
            private UUID menuCategoryId;
            private String name;
            private String content;
            private Integer price;
            private String menuPicture;
            private String menuCategory;
            private String menuStatus;
            private List<MenuOptionDto> menuOptions;
            private LocalDateTime createdAt;
        }

        @Getter
        @Builder
        public static class ReviewDto {
            private UUID id;
            private Double score;
            private String content;
        }

        @Getter
        @Builder
        public static class MenuOptionDto {
            private UUID id;
            private Integer additionalPrice;
            private String optionName;
        }
    }
}
