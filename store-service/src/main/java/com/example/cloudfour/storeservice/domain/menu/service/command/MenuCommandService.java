package com.example.cloudfour.storeservice.domain.menu.service.command;

import com.example.cloudfour.storeservice.domain.menu.converter.MenuConverter;
import com.example.cloudfour.storeservice.domain.menu.converter.MenuOptionConverter;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import com.example.cloudfour.storeservice.domain.menu.entity.MenuCategory;
import com.example.cloudfour.storeservice.domain.menu.entity.MenuOption;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuException;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuRepository;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuOptionRepository;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MenuCommandService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuOptionRepository menuOptionRepository;

    public MenuResponseDTO.MenuDetailResponseDTO createMenu(
            MenuRequestDTO.MenuCreateRequestDTO requestDTO,
            UUID storeId,
            UUID userId
    ) {
        if(userId==null){
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        Store store = storeRepository.findByIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getOwnerId().equals(userId)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        MenuCategory menuCategory = menuCategoryRepository.findByCategory(requestDTO.getMenuCommonMainRequestDTO().getCategory())
                .orElseGet(() -> menuCategoryRepository.save(
                        MenuCategory.builder().category(requestDTO.getMenuCommonMainRequestDTO().getCategory()).build()
                ));

        if (menuRepository.existsByNameAndStoreId(requestDTO.getMenuCommonMainRequestDTO().getName(), store.getId())) {
            throw new MenuException(MenuErrorCode.ALREADY_ADD);
        }

        Menu menu = MenuConverter.toMenu(requestDTO);
        menu.setStore(store);
        menu.setMenuCategory(menuCategory);


        Menu savedMenu = menuRepository.save(menu);
        return MenuConverter.toMenuDetail1ResponseDTO(savedMenu);

    }

    
    public MenuResponseDTO.MenuDetailResponseDTO updateMenu(
            UUID menuId,
            MenuRequestDTO.MenuUpdateRequestDTO requestDTO,
            UUID userId
    ) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menu.getStore().getOwnerId().equals(userId)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        MenuCategory menuCategory = menuCategoryRepository.findByCategory(requestDTO.getMenuCommonMainRequestDTO().getCategory())
                .orElseGet(() -> menuCategoryRepository.save(
                        MenuCategory.builder()
                                .category(requestDTO.getMenuCommonMainRequestDTO().getCategory())
                                .build()

                ));

        menu.updateMenuInfo(
                requestDTO.getMenuCommonMainRequestDTO().getContent(),
                requestDTO.getMenuCommonMainRequestDTO().getName(),
                requestDTO.getMenuCommonMainRequestDTO().getPrice(),
                requestDTO.getMenuCommonMainRequestDTO().getMenuPicture(),
                requestDTO.getMenuCommonMainRequestDTO().getStatus()
        );
        menu.setMenuCategory(menuCategory);

        Menu updatedMenu = menuRepository.save(menu);
        return MenuConverter.toMenuDetail1ResponseDTO(updatedMenu);

    }

    
    public void deleteMenu(UUID menuId, UUID userId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menu.getStore().getOwnerId().equals(userId)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        menuRepository.delete(menu);
        log.info("메뉴 ID: {}가 삭제되었습니다.", menuId);
    }

    public MenuOptionResponseDTO.MenuOptionDetailResponseDTO createMenuOption(
            MenuRequestDTO.MenuOptionCreateRequestDTO requestDTO,
            UUID userId, UUID menuId
    ) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menu.getStore().getOwnerId().equals(userId)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (menuOptionRepository.existsByMenuIdAndOptionName(menu.getId(), requestDTO.getMenuOptionCommonRequestDTO().getOptionName())) {
            throw new MenuException(MenuErrorCode.ALREADY_ADD);
        }

        MenuOption menuOption = MenuOption.builder()
                .optionName(requestDTO.getMenuOptionCommonRequestDTO().getOptionName())
                .additionalPrice(requestDTO.getMenuOptionCommonRequestDTO().getAdditionalPrice())
                .build();

        menuOption.setMenu(menu);

        return MenuOptionConverter.toMenuOptionDetailResponseDTO(menuOption);
    }
    
    public MenuOptionResponseDTO.MenuOptionDetailResponseDTO updateMenuOption(
            UUID optionId,
            MenuRequestDTO.MenuOptionUpdateRequestDTO requestDTO,
            UUID userId
    ) {
        MenuOption menuOption = menuOptionRepository.findByIdWithMenu(optionId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menuOption.getMenu().getStore().getOwnerId().equals(userId)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (!menuOption.getOptionName().equals(requestDTO.getMenuOptionCommonRequestDTO().getOptionName()) &&
                menuOptionRepository.existsByMenuIdAndOptionName(menuOption.getMenu().getId(), requestDTO.getMenuOptionCommonRequestDTO().getOptionName())) {
            throw new MenuException(MenuErrorCode.ALREADY_ADD);
        }

        menuOption.updateOptionInfo(requestDTO.getMenuOptionCommonRequestDTO().getOptionName(), requestDTO.getMenuOptionCommonRequestDTO().getAdditionalPrice());
        MenuOption savedOption = menuOptionRepository.save(menuOption);

        return MenuOptionConverter.toMenuOptionDetailResponseDTO(savedOption);
    }

    
    public void deleteMenuOption(UUID optionId, UUID userId) {
        MenuOption menuOption = menuOptionRepository.findByIdWithMenu(optionId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        if (!menuOption.getMenu().getStore().getOwnerId().equals(userId)) {
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        menuOptionRepository.delete(menuOption);
        log.info("메뉴 옵션 ID: {}가 삭제되었습니다.", optionId);
    }
}
