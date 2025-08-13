package com.example.cloudfour.storeservice.domain.store.service.command;

import com.example.cloudfour.storeservice.config.GatewayPrincipal;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuException;
import com.example.cloudfour.storeservice.domain.region.entity.Region;
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

    public StoreResponseDTO.StoreCreateResponseDTO createStore(
            StoreRequestDTO.StoreCreateRequestDTO dto,
            GatewayPrincipal user
    ) {

        if(user==null){
            throw new MenuException(MenuErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (storeRepository.existsByName(dto.getStoreCommonRequestDTO().getName())) {
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }

        StoreCategory category = storeCategoryRepository
                .findByCategory(dto.getStoreCommonRequestDTO().getCategory())
                .orElseGet(() -> storeCategoryRepository.save(
                        StoreCategoryConverter.toStoreCategory(dto.getStoreCommonRequestDTO().getCategory())
                ));

        UUID regionId = regionService.parseAndSaveRegion(dto.getStoreCommonRequestDTO().getAddress());
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        Store store = StoreConverter.toStore(dto);
        store.setStoreCategory(category);
        store.setRegion(region);
        store.setOwnerId(user.userId());

        storeRepository.save(store);
        return StoreConverter.toStoreCreateResponseDTO(store);
    }

    public StoreResponseDTO.StoreUpdateResponseDTO updateStore(
            UUID storeId,
            StoreRequestDTO.StoreUpdateRequestDTO dto,
            GatewayPrincipal user
    ) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getOwnerId().equals(user.userId())) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (dto.getStoreCommonRequestDTO().getName() != null && storeRepository.existsByName(dto.getStoreCommonRequestDTO().getName())) {
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }

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
        return StoreConverter.toStoreUpdateResponseDTO(store);
    }

    
    public void deleteStore(UUID storeId, GatewayPrincipal user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getOwnerId().equals(user.userId())) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        store.softDelete();
        storeRepository.save(store);
    }
}
