package com.example.cloudfour.storeservice.domain.menu.dto;

import com.example.cloudfour.storeservice.domain.menu.controller.MenuOptionCommonResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class MenuOptionResponseDTO {
    @Getter
    @Builder
    public static class MenuOptionListResponseDTO{
        MenuOptionCommonResponseDTO menuOptionCommonResponseDTO;
    }

    @Getter
    @Builder
    public static class MenuOptionDetailResponseDTO {
        MenuOptionCommonResponseDTO menuOptionCommonResponseDTO;
    }

    @Getter
    @Builder
    public static class MenuOptionsByMenuResponseDTO {
        private List<MenuOptionSimpleResponseDTO> options;
    }

    @Getter
    @Builder
    public static class MenuOptionSimpleResponseDTO {
        MenuOptionCommonResponseDTO menuOptionCommonResponseDTO;
    }
}
