package com.example.cloudfour.storeservice.domain.menu.dto;

import com.example.cloudfour.storeservice.domain.menu.controller.MenuCommonResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class MenuResponseDTO {

    @Getter
    @Builder
    public static class MenuDetailResponseDTO {
        MenuCommonResponseDTO menuCommonResponseDTO;
        private String storeName;
        private String content;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        private java.util.List<MenuOptionDTO> menuOptions;
    }

    @Getter
    @Builder
    public static class MenuOptionDTO {
        private UUID menuOptionId;
        private String optionName;
        private Integer additionalPrice;
    }

    @Getter
    @Builder
    public static class MenuListResponseDTO {
        MenuCommonResponseDTO menuCommonResponseDTO;
        private java.time.LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class MenuTopResponseDTO {
        MenuCommonResponseDTO menuCommonResponseDTO;
        private String storeName;
    }

    @Getter
    @Builder
    public static class MenuTimeTopResponseDTO {
        MenuCommonResponseDTO menuCommonResponseDTO;
        private String storeName;
        private Integer orderCount;
    }

    @Getter
    @Builder
    public static class MenuRegionTopResponseDTO {
        MenuCommonResponseDTO menuCommonResponseDTO;
        private String storeName;
        private String region;
    }

    @Getter
    @Builder
    public static class MenuStoreListResponseDTO {
        private java.util.List<MenuListResponseDTO> menus;
        private Boolean hasNext;
        private java.time.LocalDateTime nextCursor;
    }
}