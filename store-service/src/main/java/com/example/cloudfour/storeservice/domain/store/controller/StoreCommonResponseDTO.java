package com.example.cloudfour.storeservice.domain.store.controller;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreCommonResponseDTO {
    @Getter
    @Builder
    public static class StoreCommonMainResponseDTO{
        private String phone;
        private String content;
        private Integer minPrice;
        private Integer deliveryTip;
        private String operationHours;
        private String closedDays;
        private String category;
    }
    @Getter
    @Builder
    public static class StoreCommonOptionResponseDTO{
        private float rating;
        private int reviewCount;
    }
}
