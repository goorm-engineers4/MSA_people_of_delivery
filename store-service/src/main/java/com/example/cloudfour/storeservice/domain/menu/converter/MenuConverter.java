package com.example.cloudfour.storeservice.domain.menu.converter;

import com.example.cloudfour.storeservice.domain.menu.controller.MenuCommonResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import com.example.cloudfour.storeservice.domain.menu.entity.MenuOption;

import java.util.List;

public class MenuConverter {

    public static Menu toMenu(MenuRequestDTO.MenuCreateRequestDTO requestDTO) {
        return Menu.builder()
                .name(requestDTO.getMenuCommonMainRequestDTO().getName())
                .content(requestDTO.getMenuCommonMainRequestDTO().getContent())
                .price(requestDTO.getMenuCommonMainRequestDTO().getPrice())
                .menuPicture(requestDTO.getMenuCommonMainRequestDTO().getMenuPicture())
                .status(requestDTO.getMenuCommonMainRequestDTO().getStatus())
                .build();
    }

    public static MenuResponseDTO.MenuDetailResponseDTO toMenuDetail1ResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .storeName(menu.getStore().getName())
                .content(menu.getContent())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    public static MenuResponseDTO.MenuDetailResponseDTO toMenuDetail2ResponseDTO(
            Menu menu, List<MenuResponseDTO.MenuOptionDTO> options) {
        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .storeName(menu.getStore().getName())
                .content(menu.getContent())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .menuOptions(options)
                .build();
    }

    public static MenuResponseDTO.MenuOptionDTO toMenuOptionDTO(MenuOption option) {
        return MenuResponseDTO.MenuOptionDTO.builder()
                .menuOptionId(option.getId())
                .optionName(option.getOptionName())
                .additionalPrice(option.getAdditionalPrice())
                .build();
    }

    public static MenuResponseDTO.MenuListResponseDTO toMenuListResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuListResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .createdAt(menu.getCreatedAt())
                .build();
    }

    public static MenuResponseDTO.MenuTopResponseDTO toMenuTopResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuTopResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .storeName(menu.getStore().getName())
                .build();
    }


    public static MenuResponseDTO.MenuTimeTopResponseDTO toMenuTimeTopResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuTimeTopResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .build();
    }

    public static MenuResponseDTO.MenuRegionTopResponseDTO toMenuRegionTopResponseDTO(Menu menu) {
        var store = menu.getStore();

        String regionStr = null;
        if (store.getAddress() != null) {
            String[] parts = store.getAddress().trim().split("\\s+");
            if (parts.length >= 2) regionStr = parts[0] + " " + parts[1];
        }

        return MenuResponseDTO.MenuRegionTopResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .storeName(store.getName())
                .region(regionStr)
                .build();
    }


    public static MenuCommonResponseDTO toMenuCommonResponseDTO(Menu menu) {
        return MenuCommonResponseDTO.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .category(menu.getMenuCategory().getCategory())
                .build();
    }
}
