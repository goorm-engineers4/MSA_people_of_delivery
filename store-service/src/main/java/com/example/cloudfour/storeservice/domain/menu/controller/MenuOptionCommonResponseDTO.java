package com.example.cloudfour.storeservice.domain.menu.controller;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class MenuOptionCommonResponseDTO {
    String optionName;
    Integer additionalPrice;
    private UUID menuId;
    private String menuName;
    private String storeName;
    private UUID menuOptionId;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

}
