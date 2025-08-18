package com.example.cloudfour.storeservice.domain.menu.converter;

import com.example.cloudfour.storeservice.domain.collection.document.StoreDocument;
import com.example.cloudfour.storeservice.domain.menu.controller.MenuOptionCommonResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.MenuOption;

public class MenuOptionConverter {
    public static MenuOptionResponseDTO.MenuOptionSimpleResponseDTO toMenuOptionSimpleResponseDTO(MenuOption option) {
        return MenuOptionResponseDTO.MenuOptionSimpleResponseDTO.builder()
                .menuOptionId(option.getId())
                .additionalPrice(option.getAdditionalPrice())
                .optionName(option.getOptionName())
                .build();
    }

    public static MenuOptionResponseDTO.MenuOptionSimpleResponseDTO documentToMenuOptionSimpleResponseDTO(StoreDocument.MenuOption option) {
        return MenuOptionResponseDTO.MenuOptionSimpleResponseDTO.builder()
                .menuOptionId(option.getId())
                .additionalPrice(option.getAdditionalPrice())
                .optionName(option.getOptionName())
                .build();
    }


    public static MenuOptionCommonResponseDTO toMenuOptionCommonResponseDTO(MenuOption option) {
        return MenuOptionCommonResponseDTO.builder()
                .additionalPrice(option.getAdditionalPrice())
                .optionName(option.getOptionName())
                .build();
    }
}