package com.example.cloudfour.storeservice.domain.menu.dto;

import com.example.cloudfour.storeservice.domain.menu.controller.MenuCommonRequestDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

public class MenuRequestDTO {

    @Getter
    @Builder
    public static class MenuCreateRequestDTO {
        @JsonUnwrapped
        MenuCommonRequestDTO.MenuCommonMainRequestDTO menuCommonMainRequestDTO;
    }

    @Getter
    @Builder
    public static class MenuUpdateRequestDTO {
        @JsonUnwrapped
        MenuCommonRequestDTO.MenuCommonMainRequestDTO menuCommonMainRequestDTO;
    }

    @Getter
    @Builder
    public static class MenuOptionCreateRequestDTO {
        @JsonUnwrapped
        MenuCommonRequestDTO.MenuOptionCommonRequestDTO menuOptionCommonRequestDTO;
    }

    @Getter
    @Builder
    public static class MenuOptionUpdateRequestDTO {
        @JsonUnwrapped
        MenuCommonRequestDTO.MenuOptionCommonRequestDTO menuOptionCommonRequestDTO;
    }

}
