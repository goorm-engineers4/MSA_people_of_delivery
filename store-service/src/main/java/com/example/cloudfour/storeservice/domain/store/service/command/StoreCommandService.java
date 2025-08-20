package com.example.cloudfour.storeservice.domain.store.service.command;

import com.example.cloudfour.modulecommon.dto.CurrentUser;
import com.example.cloudfour.storeservice.domain.collection.document.StoreDocument;
import com.example.cloudfour.storeservice.domain.collection.repository.command.StoreCommandRepository;
import com.example.cloudfour.storeservice.domain.menu.enums.MenuStatus;
import com.example.cloudfour.storeservice.domain.region.entity.Region;
import com.example.cloudfour.storeservice.domain.region.exception.RegionErrorCode;
import com.example.cloudfour.storeservice.domain.region.exception.RegionException;
import com.example.cloudfour.storeservice.domain.region.service.RegionService;
import com.example.cloudfour.storeservice.domain.region.repository.RegionRepository;
import com.example.cloudfour.storeservice.domain.store.converter.StoreCategoryConverter;
import com.example.cloudfour.storeservice.domain.store.converter.StoreConverter;
import com.example.cloudfour.storeservice.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.entity.StoreCategory;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreCategoryRepository;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreCommandService {

    private final StoreRepository storeRepository;
    private final RegionRepository regionRepository;
    private final RegionService regionService;
    private final StoreCategoryRepository storeCategoryRepository;
    private final StoreCommandRepository storeCommandRepository;

    public StoreResponseDTO.testResponseDTO    createStoreTest(
            StoreRequestDTO.testRequestDTO dto,
            CurrentUser user
    ) {

        if(user==null){
            log.warn("가게 생성 권한 없음");
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        StoreDocument storeDocument = StoreDocument.builder()
                .storeId(dto.getStoreId())
                .storeCategoryId(dto.getStoreCategoryId())
                .userId(dto.getUserId())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .content(dto.getContent())
                .minPrice(dto.getMinPrice())
                .deliveryTip(dto.getDeliveryTip())
                .rating(dto.getRating())
                .likeCount(dto.getLikeCount())
                .reviewCount(dto.getReviewCount())
                .OperationHours(dto.getOperationHours())
                .closedDays(dto.getClosedDays())
                .storeCategory(dto.getStoreCategory())
                .siDo(dto.getSiDo())
                .siGunGu(dto.getSiGunGu())
                .eupMyeonDong(dto.getEupMyeonDong())
                .pictureURL(dto.getPictureURL())
                .createdAt(dto.getCreatedAt())
                .menus(dto.getMenus() != null ?
                        dto.getMenus().stream()
                                .map(menuDto -> StoreDocument.Menu.builder()
                                        .id(menuDto.getId())
                                        .menuCategoryId(menuDto.getMenuCategoryId())
                                        .name(menuDto.getName())
                                        .content(menuDto.getContent())
                                        .price(menuDto.getPrice())
                                        .menuPicture(menuDto.getMenuPicture())
                                        .menuCategory(menuDto.getMenuCategory())
                                        .menuStatus(menuDto.getMenuStatus() != null ?
                                                MenuStatus.valueOf(menuDto.getMenuStatus()) : null)
                                        .menuOptions(menuDto.getMenuOptions() != null ?
                                                menuDto.getMenuOptions().stream()
                                                        .map(optionDto -> StoreDocument.MenuOption.builder()
                                                                .id(optionDto.getId())
                                                                .additionalPrice(optionDto.getAdditionalPrice())
                                                                .optionName(optionDto.getOptionName())
                                                                .build())
                                                        .toList()
                                                : null)
                                        .createdAt(menuDto.getCreatedAt())
                                        .build())
                                .toList()
                        : null)
                .reviews(dto.getReviews() != null ?
                        dto.getReviews().stream()
                                .map(reviewDto -> StoreDocument.Review.builder()
                                        .id(reviewDto.getId())
                                        .score(reviewDto.getScore())
                                        .content(reviewDto.getContent())
                                        .build())
                                .toList()
                        : null)
                .build();
        storeCommandRepository.save(storeDocument);
        return StoreResponseDTO.testResponseDTO.builder()
                .storeId(storeDocument.getStoreId())
                .storeCategoryId(storeDocument.getStoreCategoryId())
                .userId(storeDocument.getUserId())
                .name(storeDocument.getName())
                .address(storeDocument.getAddress())
                .phone(storeDocument.getPhone())
                .content(storeDocument.getContent())
                .minPrice(storeDocument.getMinPrice())
                .deliveryTip(storeDocument.getDeliveryTip())
                .rating(storeDocument.getRating())
                .likeCount(storeDocument.getLikeCount())
                .reviewCount(storeDocument.getReviewCount())
                .operationHours(storeDocument.getOperationHours()) // Dto에서는 소문자
                .closedDays(storeDocument.getClosedDays())
                .storeCategory(storeDocument.getStoreCategory())
                .siDo(storeDocument.getSiDo())
                .siGunGu(storeDocument.getSiGunGu())
                .eupMyeonDong(storeDocument.getEupMyeonDong())
                .pictureURL(storeDocument.getPictureURL())
                .createdAt(storeDocument.getCreatedAt())
                .menus(storeDocument.getMenus() != null ?
                        storeDocument.getMenus().stream()
                                .map(menu -> StoreResponseDTO.testResponseDTO.MenuDto.builder()
                                        .id(menu.getId())
                                        .menuCategoryId(menu.getMenuCategoryId())
                                        .name(menu.getName())
                                        .content(menu.getContent())
                                        .price(menu.getPrice())
                                        .menuPicture(menu.getMenuPicture())
                                        .menuCategory(menu.getMenuCategory())
                                        .menuStatus(menu.getMenuStatus() != null ?
                                                menu.getMenuStatus().name() : null) // Enum → String 변환
                                        .menuOptions(menu.getMenuOptions() != null ?
                                                menu.getMenuOptions().stream()
                                                        .map(option -> StoreResponseDTO.testResponseDTO.MenuOptionDto.builder()
                                                                .id(option.getId())
                                                                .additionalPrice(option.getAdditionalPrice())
                                                                .optionName(option.getOptionName())
                                                                .build())
                                                        .toList()
                                                : null)
                                        .createdAt(menu.getCreatedAt())
                                        .build())
                                .toList()
                        : null)
                .reviews(storeDocument.getReviews() != null ?
                        storeDocument.getReviews().stream()
                                .map(review -> StoreResponseDTO.testResponseDTO.ReviewDto.builder()
                                        .id(review.getId())
                                        .score(review.getScore())
                                        .content(review.getContent())
                                        .build())
                                .toList()
                        : null)
                .build();
    }

    public StoreResponseDTO.StoreCreateResponseDTO createStore(
            StoreRequestDTO.StoreCreateRequestDTO dto,
            CurrentUser user
    ) {

        if(user==null){
            log.warn("가게 생성 권한 없음");
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (storeRepository.existsByNameAndIsDeletedFalse(dto.getStoreCommonRequestDTO().getName())) {
            log.warn("이미 존재하는 가게 이름");
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }

        log.info("가게 저장 권한 확인 성공");
        StoreCategory category = storeCategoryRepository
                .findByCategory(dto.getStoreCommonRequestDTO().getCategory())
                .orElseGet(() -> storeCategoryRepository.save(
                        StoreCategoryConverter.toStoreCategory(dto.getStoreCommonRequestDTO().getCategory())
                ));

        UUID regionId = regionService.parseAndSaveRegion(dto.getStoreCommonRequestDTO().getAddress());
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 지역");
                    return new RegionException(RegionErrorCode.NOT_FOUND);
                });

        Store store = StoreConverter.toStore(dto);
        store.setStoreCategory(category);
        store.setRegion(region);
        store.setOwnerId(user.id());

        storeRepository.save(store);
        log.info("가게 저장 성공");
        return StoreConverter.toStoreCreateResponseDTO(store);
    }

    public StoreResponseDTO.StoreUpdateResponseDTO updateStore(
            UUID storeId,
            StoreRequestDTO.StoreUpdateRequestDTO dto,
            CurrentUser user
    ) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() ->{
                    log.warn("존재하지 않는 가게");
                    return new StoreException(StoreErrorCode.NOT_FOUND);
                });

        if (user == null || !store.getOwnerId().equals(user.id())) {
            log.warn("가게 수정 권한 없음");
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (dto.getStoreCommonRequestDTO().getName() != null && storeRepository.existsByNameAndIsDeletedFalse(dto.getStoreCommonRequestDTO().getName())) {
            log.warn("이미 존재하는 가게 이름");
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }
        log.info("가게 수정 권한 확인 성공");
        if (dto.getStoreCommonRequestDTO().getCategory() != null) {
            StoreCategory category = storeCategoryRepository
                    .findByCategory(dto.getStoreCommonRequestDTO().getCategory())
                    .orElseGet(() -> storeCategoryRepository.save(
                            StoreCategoryConverter.toStoreCategory(dto.getStoreCommonRequestDTO().getCategory())
                    ));
            store.setStoreCategory(category);
        }

        store.update(dto.getStoreCommonRequestDTO().getName(), dto.getStoreCommonRequestDTO().getAddress());
        storeRepository.save(store);
        log.info("가게 수정 성공");
        return StoreConverter.toStoreUpdateResponseDTO(store);
    }

    
    public void deleteStore(UUID storeId, CurrentUser user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 가게");
                    return new StoreException(StoreErrorCode.NOT_FOUND);
                });

        if (user==null || !store.getOwnerId().equals(user.id())) {
            log.warn("가게 삭제 권한 없음");
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("가게 삭제 권한 확인 성공");
        store.softDelete();
        storeRepository.save(store);
        log.info("가게 삭제 성공");
    }
}
