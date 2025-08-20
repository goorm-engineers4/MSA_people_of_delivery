package com.example.cloudfour.storeservice.domain.store.dto;

import com.example.cloudfour.storeservice.domain.store.controller.StoreCommonResponseDTO;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class StoreResponseDTO {

    @Getter
    @SuperBuilder
    public static abstract class StoreBaseResponseDTO {
        @JsonUnwrapped
        StoreCommonResponseDTO.StoreCommonsBaseResponseDTO storeCommonsBaseResponseDTO;
    }

    @Getter
    @SuperBuilder
    public static class StoreCreateResponseDTO extends StoreBaseResponseDTO {
        @JsonUnwrapped
        StoreCommonResponseDTO.StoreCommonMainResponseDTO storeCommonMainResponseDTO;
        private LocalDateTime createdAt;
        private UUID createdBy;
    }

    @Getter
    @SuperBuilder
    public static class StoreUpdateResponseDTO extends StoreBaseResponseDTO {
        private String category;
        private LocalDateTime updatedAt;
    }

    @Getter
    @SuperBuilder
    public static class StoreListResponseDTO extends StoreBaseResponseDTO {
        @JsonUnwrapped
        StoreCommonResponseDTO.StoreCommonOptionResponseDTO storeCommonOptionResponseDTO;
        private LocalDateTime createdAt;
    }

    @Getter
    @SuperBuilder
    public static class StoreCursorListResponseDTO {
        private List<StoreListResponseDTO> storeList;
        private LocalDateTime nextCursor;

        public static StoreCursorListResponseDTO of(List<StoreListResponseDTO> storeList, LocalDateTime nextCursor) {
            return StoreCursorListResponseDTO.builder()
                    .storeList(storeList)
                    .nextCursor(nextCursor)
                    .build();
        }
    }

    @Getter
    @SuperBuilder
    public static class StoreDetailResponseDTO extends StoreBaseResponseDTO {
        UUID userId;
        @JsonUnwrapped
        StoreCommonResponseDTO.StoreCommonMainResponseDTO storeCommonMainResponseDTO;
        @JsonUnwrapped
        StoreCommonResponseDTO.StoreCommonOptionResponseDTO storeCommonOptionResponseDTO;
    }

    @Getter
    @Builder
    public static class testResponseDTO{
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
        private List<testResponseDTO.MenuDto> menus;
        private List<testResponseDTO.ReviewDto> reviews;

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
            private String menuStatus; // Enum은 String으로 받는게 편함
            private List<testResponseDTO.MenuOptionDto> menuOptions;
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
