package com.example.cloudfour.storeservice.domain.menu.service.query;

import com.example.cloudfour.storeservice.domain.menu.converter.MenuConverter;
import com.example.cloudfour.storeservice.domain.menu.converter.MenuOptionConverter;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuQueryService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final StoreRepository storeRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final RestTemplate restTemplate;

    private static final LocalDateTime FIRST_CURSOR = LocalDateTime.now().plusDays(1);

    public MenuResponseDTO.MenuStoreListResponseDTO getMenusByStoreWithCursor(
            UUID storeId, LocalDateTime cursor, Integer size, UUID userId
    ) {
        storeRepository.findByIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (cursor == null) cursor = FIRST_CURSOR;

        Pageable pageable = PageRequest.of(0, size);
        Slice<Menu> menuSlice =
                menuRepository.findByStoreIdAndDeletedFalseAndCreatedAtBefore(storeId, cursor, pageable);

        if (menuSlice.isEmpty()) throw new MenuException(MenuErrorCode.NOT_FOUND);

        List<MenuResponseDTO.MenuListResponseDTO> menus = menuSlice.getContent().stream()
                .map(MenuConverter::toMenuListResponseDTO)
                .toList();

        LocalDateTime nextCursor = (menuSlice.hasNext() && !menuSlice.isEmpty())
                ? menuSlice.getContent().getLast().getCreatedAt()
                : null;

        return MenuResponseDTO.MenuStoreListResponseDTO.builder()
                .menus(menus)
                .hasNext(menuSlice.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

    public MenuResponseDTO.MenuStoreListResponseDTO getMenusByStoreWithCategory(
            UUID storeId, UUID categoryId, LocalDateTime cursor, Integer size, UUID userId
    ) {
        storeRepository.findByIdAndIsDeletedFalse(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new MenuCategoryException(MenuCategoryErrorCode.NOT_FOUND));

        if (cursor == null) cursor = FIRST_CURSOR;

        Pageable pageable = PageRequest.of(0, size);
        Slice<Menu> menuSlice =
                menuRepository.findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(
                        storeId, categoryId, cursor, pageable);

        if (menuSlice.isEmpty()) throw new MenuException(MenuErrorCode.NOT_FOUND);

        List<MenuResponseDTO.MenuListResponseDTO> menus = menuSlice.getContent().stream()
                .map(MenuConverter::toMenuListResponseDTO)
                .toList();

        LocalDateTime nextCursor = (menuSlice.hasNext() && !menuSlice.isEmpty())
                ? menuSlice.getContent().getLast().getCreatedAt()
                : null;

        return MenuResponseDTO.MenuStoreListResponseDTO.builder()
                .menus(menus)
                .hasNext(menuSlice.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

//    public List<MenuResponseDTO.MenuTopResponseDTO> getTopMenus(UUID userId) {
//        OrderItemResponseDTO orderItemResponseDTO = restTemplate.getForObject("http://order-service/api/order-items/{userId}", OrderItemResponseDTO.class, userId);
//        return menuRepository.findTopMenusByOrderCount(PageRequest.of(0, 20))
//                .stream()
//                .map(MenuConverter::toMenuTopResponseDTO)
//                .toList();
//    }
//
//    public List<MenuResponseDTO.MenuTimeTopResponseDTO> getTimeTopMenus(UUID userId) {
//        LocalDateTime startTime = LocalDateTime.now().minusHours(24);
//        LocalDateTime endTime = LocalDateTime.now();
//
//        return menuRepository.findTopMenusByTimeRange(startTime, endTime, PageRequest.of(0, 20))
//                .stream()
//                .map(MenuConverter::toMenuTimeTopResponseDTO)
//                .toList();
//    }
//
//    public List<MenuResponseDTO.MenuRegionTopResponseDTO> getRegionTopMenus(String si, String gu, UUID userId) {
//        return menuRepository.findTopMenusByRegion(si, gu, PageRequest.of(0, 20))
//                .stream()
//                .map(MenuConverter::toMenuRegionTopResponseDTO)
//                .toList();
//    }

    public MenuResponseDTO.MenuDetailResponseDTO getMenuDetail(UUID menuId, UUID userId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        var optionDTOs = menuOptionRepository.findByMenuIdOrderByAdditionalPrice(menuId)
                .stream()
                .map(MenuConverter::toMenuOptionDTO)
                .toList();

        return MenuConverter.toMenuDetail2ResponseDTO(menu, optionDTOs);
    }

    public MenuOptionResponseDTO.MenuOptionsByMenuResponseDTO getMenuOptionsByMenu(UUID menuId, UUID userId) {
        menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        var options = menuOptionRepository.findByMenuIdOrderByAdditionalPrice(menuId)
                .stream()
                .map(MenuOptionConverter::toMenuOptionSimpleResponseDTO)
                .toList();

        return MenuOptionResponseDTO.MenuOptionsByMenuResponseDTO.builder()
                .options(options)
                .build();
    }

    public MenuOptionResponseDTO.MenuOptionDetailResponseDTO getMenuOptionDetail(UUID optionId, UUID userId) {
        var menuOption = menuOptionRepository.findByIdWithMenu(optionId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.NOT_FOUND));

        return MenuOptionConverter.toMenuOptionDetailResponseDTO(menuOption);
    }
}
