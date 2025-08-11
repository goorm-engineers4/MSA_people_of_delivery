package com.example.cloudfour.storeservice.domain.menu.dto;

import com.example.cloudfour.storeservice.domain.menu.controller.MenuCommonRequestDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class MenuRequestDTO {

    @Getter
    @Builder
    public static class MenuCreateRequestDTO {
        MenuCommonRequestDTO.MenuCommonMainRequestDTO menuCommonMainRequestDTO;
    }

    @Getter
    @Builder
    public static class MenuUpdateRequestDTO {
        MenuCommonRequestDTO.MenuCommonMainRequestDTO menuCommonMainRequestDTO;
    }

    @Getter
    @Builder
    public static class MenuOptionCreateRequestDTO {
        MenuCommonRequestDTO.MenuOptionCommonRequestDTO menuOptionCommonRequestDTO;
    }

    @Getter
    @Builder
    public static class MenuOptionUpdateRequestDTO {
        private UUID optionId;
        MenuCommonRequestDTO.MenuOptionCommonRequestDTO menuOptionCommonRequestDTO;
    }

}
