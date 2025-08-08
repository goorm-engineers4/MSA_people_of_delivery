package com.example.cloudfour.storeservice.domain.menu.service.query;

import com.example.cloudfour.storeservice.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import com.example.cloudfour.storeservice.domain.menu.entity.MenuOption;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuCategoryErrorCode;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuCategoryException;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuException;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuCategoryRepository;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuRepository;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuOptionRepository;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuQueryService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);
    private final StoreRepository storeRepository;
    private final MenuOptionRepository menuOptionRepository;

    /** 매장별 메뉴 조회 (커서 기반) */
    public MenuResponseDTO.MenuStoreListResponseDTO getMenusByStoreWithCursor(
            UUID storeId, LocalDateTime cursor, Integer size, UUID userId
    ) {
        storeRepository.findByIdAndIsDeletedFalse(storeId).orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (cursor == null) {
            cursor = first_cursor;
        }

        Pageable pageable = PageRequest.of(0, size);
        Slice<Menu> menuSlice = menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(storeId, cursor, pageable);

        if (menuSlice.isEmpty()) {
            throw new MenuException(MenuErrorCode.NOT_FOUND);
        }

        List<MenuResponseDTO.MenuListResponseDTO> menuDTOS = menuSlice.getContent().stream()
                .map(menu -> MenuResponseDTO.MenuListResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .status(menu.getStatus())
                        .category(menu.getMenuCategory().getCategory())
                        .createdAt(menu.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        LocalDateTime next_cursor = (!menuSlice.isEmpty() && menuSlice.hasNext())
                ? menuSlice.getContent().getLast().getCreatedAt()
                : null;

        return MenuResponseDTO.MenuStoreListResponseDTO.builder()
                .menus(menuDTOS)
                .hasNext(menuSlice.hasNext())
                .nextCursor(next_cursor)
                .build();
    }

    /** 매장 + 카테고리별 메뉴 조회 */
    public MenuResponseDTO.MenuStoreListResponseDTO getMenusByStoreWithCategory(
            UUID storeId, UUID categoryId, LocalDateTime cursor, Integer size, UUID userId
    ) {
        storeRepository.findByIdAndIsDeletedFalse(storeId).orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        menuCategoryRepository.findById(categoryId).orElseThrow(() -> new MenuCategoryException(MenuCategoryErrorCode.NOT_FOUND));

        if (cursor == null) {
            cursor = first_cursor;
        }

        Pageable pageable = PageRequest.of(0, size);
        Slice<Menu> menuSlice = menuRepository.findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(
                storeId, categoryId, cursor, pageable
        );

        if (menuSlice.isEmpty()) {
            throw new MenuException(MenuErrorCode.NOT_FOUND);
        }

        List<MenuResponseDTO.MenuListResponseDTO> menuDTOS = menuSlice.getContent().stream()
                .map(menu -> MenuResponseDTO.MenuListResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .status(menu.getStatus())
                        .category(menu.getMenuCategory().getCategory())
                        .createdAt(menu.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        LocalDateTime next_cursor = (!menuSlice.isEmpty() && menuSlice.hasNext())
                ? menuSlice.getContent().getLast().getCreatedAt()
                : null;

        return MenuResponseDTO.MenuStoreListResponseDTO.builder()
                .menus(menuDTOS)
                .hasNext(menuSlice.hasNext())
                .nextCursor(next_cursor)
                .build();
    }

    /** 전체 인기 메뉴 Top20 */
    public List<MenuResponseDTO.MenuTopResponseDTO> getTopMenus(UUID userId) {

        return menuRepository.findTopMenusByOrderCount(PageRequest.of(0, 20))
                .stream()
                .map(menu -> MenuResponseDTO.MenuTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .storeName(menu.getStore().getName())
                        .build())
                .collect(Collectors.toList());
    }

    /** 최근 24시간 인기 메뉴 Top20 */
    public List<MenuResponseDTO.MenuTimeTopResponseDTO> getTimeTopMenus(UUID userId) {

        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
        LocalDateTime endTime = LocalDateTime.now();

        return menuRepository.findTopMenusByTimeRange(startTime, endTime, PageRequest.of(0, 20))
                .stream()
                .map(menu -> MenuResponseDTO.MenuTimeTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .storeName(menu.getStore().getName())
                        .orderCount(menu.getOrderItems().size())
                        .build())
                .collect(Collectors.toList());
    }

    /** 지역별 인기 메뉴 Top20 */
    public List<MenuResponseDTO.MenuRegionTopResponseDTO> getRegionTopMenus(String si, String gu, UUID userId) {

        return menuRepository.findTopMenusByRegion(si, gu, PageRequest.of(0, 20))
                .stream()
                .map(menu -> MenuResponseDTO.MenuRegionTopResponseDTO.builder()
                        .menuId(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .menuPicture(menu.getMenuPicture())
                        .status(menu.getStatus())
                        .category(menu.getMenuCategory().getCategory())
                        .storeName(menu.getStore().getName())
                        .region(menu.getStore().getRegion().getSi() + " " + menu.getStore().getRegion().getGu())
                        .build())
                .collect(Collectors.toList());
    }

    /** 메뉴 상세 조회 */
    public MenuResponseDTO.MenuDetailResponseDTO getMenuDetail(UUID menuId, UUID userId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        List<MenuOption> menuOptions = menuOptionRepository.findByMenuIdOrderByAdditionalPrice(menuId);

        List<MenuResponseDTO.MenuOptionDTO> menuOptionDTOs = menuOptions.stream()
                .map(option -> MenuResponseDTO.MenuOptionDTO.builder()
                        .menuOptionId(option.getId())
                        .optionName(option.getOptionName())
                        .additionalPrice(option.getAdditionalPrice())
                        .build())
                .collect(Collectors.toList());

        return MenuResponseDTO.MenuDetailResponseDTO.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .content(menu.getContent())
                .price(menu.getPrice())
                .menuPicture(menu.getMenuPicture())
                .status(menu.getStatus())
                .storeId(menu.getStore().getId())
                .storeName(menu.getStore().getName())
                .category(menu.getMenuCategory().getCategory())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .menuOptions(menuOptionDTOs)
                .build();
    }

    /** 메뉴 옵션 목록 조회 */
    public MenuOptionResponseDTO.MenuOptionsByMenuResponseDTO getMenuOptionsByMenu(UUID menuId, UUID userId) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        List<MenuOptionResponseDTO.MenuOptionSimpleResponseDTO> optionDTOs =
                menuOptionRepository.findByMenuIdOrderByAdditionalPrice(menuId)
                        .stream()
                        .map(option -> MenuOptionResponseDTO.MenuOptionSimpleResponseDTO.builder()
                                .menuOptionId(option.getId())
                                .optionName(option.getOptionName())
                                .additionalPrice(option.getAdditionalPrice())
                                .build())
                        .collect(Collectors.toList());

        return MenuOptionResponseDTO.MenuOptionsByMenuResponseDTO.builder()
                .menuId(menu.getId())
                .menuName(menu.getName())
                .options(optionDTOs)
                .build();
    }

    /** 메뉴 옵션 상세 조회 */
    public MenuOptionResponseDTO.MenuOptionDetailResponseDTO getMenuOptionDetail(UUID optionId, UUID userId) {

        MenuOption menuOption = menuOptionRepository.findByIdWithMenu(optionId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        return MenuOptionResponseDTO.MenuOptionDetailResponseDTO.builder()
                .menuOptionId(menuOption.getId())
                .menuId(menuOption.getMenu().getId())
                .menuName(menuOption.getMenu().getName())
                .storeName(menuOption.getMenu().getStore().getName())
                .optionName(menuOption.getOptionName())
                .additionalPrice(menuOption.getAdditionalPrice())
                .createdAt(menuOption.getMenu().getCreatedAt())
                .updatedAt(menuOption.getMenu().getUpdatedAt())
                .build();
    }
}
