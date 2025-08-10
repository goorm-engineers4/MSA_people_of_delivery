package com.example.cloudfour.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDescriptionRequestDTO {
    private StoreDTO store;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreDTO {
        private String name;
        private String address;
        private String storePicture;
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
    }
}