package com.example.cloudfour.storeservice.domain.menu.converter;

import com.example.cloudfour.storeservice.domain.menu.controller.MenuCommonResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.Menu;

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

    public static MenuResponseDTO.MenuDetailResponseDTO toMenuDetailResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .content(menu.getContent())
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
                .build();
    }

    public static MenuResponseDTO.MenuTimeTopResponseDTO toMenuTimeTopResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuTimeTopResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
                .build();
    }

    public static MenuResponseDTO.MenuRegionTopResponseDTO toMenuRegionTopResponseDTO(Menu menu) {
        return MenuResponseDTO.MenuRegionTopResponseDTO.builder()
                .menuCommonResponseDTO(toMenuCommonResponseDTO(menu))
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
