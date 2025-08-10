package com.example.cloudfour.storeservice.domain.menu.dto;

import com.example.cloudfour.storeservice.domain.menu.controller.MenuCommonRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class MenuRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuCreateRequestDTO {
        MenuCommonRequestDTO.MenuCommonMainRequestDTO menuCommonMainRequestDTO;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuUpdateRequestDTO {
        MenuCommonRequestDTO.MenuCommonMainRequestDTO menuCommonMainRequestDTO;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuOptionCreateRequestDTO {
        MenuCommonRequestDTO.MenuOptionCommonRequestDTO menuOptionCommonRequestDTO;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuOptionUpdateRequestDTO {
        private UUID optionId;
        MenuCommonRequestDTO.MenuOptionCommonRequestDTO menuOptionCommonRequestDTO;
    }

}
