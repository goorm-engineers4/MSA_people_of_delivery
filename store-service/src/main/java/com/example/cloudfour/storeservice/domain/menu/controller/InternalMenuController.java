package com.example.cloudfour.storeservice.domain.menu.controller;

import com.example.cloudfour.modulecommon.apiPayLoad.CustomResponse;
import com.example.cloudfour.storeservice.domain.menu.converter.MenuConverter;
import com.example.cloudfour.storeservice.domain.menu.converter.MenuOptionConverter;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import com.example.cloudfour.storeservice.domain.menu.entity.MenuOption;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuException;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuOptionErrorCode;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuOptionException;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuOptionRepository;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/menus")
public class InternalMenuController {
    private final MenuRepository menuQuery;
    private final MenuOptionRepository menuOptionQuery;

    @GetMapping("/{menuId}")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> getMenuDetail(
            @PathVariable("menuId") UUID menuId) {

        Menu findMenu = menuQuery.findById(menuId).orElseThrow(()->new MenuException(MenuErrorCode.NOT_FOUND));
        MenuResponseDTO.MenuDetailResponseDTO result  = MenuConverter.toMenuDetail1ResponseDTO(findMenu);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/{optionId}")
    public CustomResponse<MenuOptionResponseDTO.MenuOptionDetailResponseDTO> getMenuOptionDetail(
            @PathVariable("optionId") UUID optionId
    ) {

        MenuOption findMenuOption = menuOptionQuery.findById(optionId).orElseThrow(
                ()-> new MenuOptionException(MenuOptionErrorCode.NOT_FOUND)
        );
        MenuOptionResponseDTO.MenuOptionDetailResponseDTO result =
                MenuOptionConverter.toMenuOptionDetailResponseDTO(findMenuOption);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }
}
