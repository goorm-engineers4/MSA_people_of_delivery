package com.example.cloudfour.storeservice.domain.store.dto;

import com.example.cloudfour.storeservice.domain.store.controller.StoreCommonRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StoreRequestDTO {

    @Getter
    @Builder
    public static class StoreCreateRequestDTO {
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
        StoreCommonRequestDTO storeCommonRequestDTO;
    }
}
