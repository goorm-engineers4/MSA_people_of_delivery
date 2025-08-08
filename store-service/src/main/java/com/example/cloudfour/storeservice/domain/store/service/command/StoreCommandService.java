package com.example.cloudfour.storeservice.domain.store.service.command;

import com.example.cloudfour.storeservice.domain.region.entity.Region;
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
    private final StoreCategoryRepository storeCategoryRepository;

    public StoreResponseDTO.StoreCreateResponseDTO createStore(
            StoreRequestDTO.StoreCreateRequestDTO dto,
            UUID userId
    ) {
        if (storeRepository.existsByName(dto.getStoreCommonRequestDTO().getName())) {
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }

        StoreCategory category = storeCategoryRepository
                .findByCategory(dto.getStoreCommonRequestDTO().getCategory())
                .orElseGet(() -> storeCategoryRepository.save(
                        StoreCategoryConverter.toStoreCategory(dto.getStoreCommonRequestDTO().getCategory())
                ));

        Long regionId = regionService.parseAndSaveRegion(dto.getStoreCommonRequestDTO().getAddress());
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        Store store = StoreConverter.toStore(dto);
        store.setStoreCategory(category);
        store.setRegion(region);
        store.setOwnerId(userId);

        storeRepository.save(store);
        return StoreConverter.toStoreCreateResponseDTO(store);
    }

    /** 가게 수정 */
    public StoreResponseDTO.StoreUpdateResponseDTO updateStore(
            UUID storeId,
            StoreRequestDTO.StoreUpdateRequestDTO dto,
            UUID userId
    ) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        // 소유자 검증
        if (!store.getOwnerId().equals(userId)) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 이름 중복 체크 (본인 가게 제외 로직 필요 시 보강)
        if (dto.getStoreCommonRequestDTO().getName() != null && storeRepository.existsByName(dto.getStoreCommonRequestDTO().getName())) {
            throw new StoreException(StoreErrorCode.ALREADY_ADD);
        }

        // 카테고리 upsert
        if (dto.getStoreCommonRequestDTO().getCategory() != null) {
            StoreCategory category = storeCategoryRepository
                    .findByCategory(dto.getStoreCommonRequestDTO().getCategory())
                    .orElseGet(() -> storeCategoryRepository.save(
                            StoreCategoryConverter.toStoreCategory(dto.getStoreCommonRequestDTO().getCategory())
                    ));
            store.setStoreCategory(category);
        }

        // 필드 부분 업데이트
        store.update(dto.getStoreCommonRequestDTO().getName(), dto.getStoreCommonRequestDTO().getAddress());
        // TODO: 주소 변경 시 Region 매핑/생성 로직 추가

        storeRepository.save(store);
        return StoreConverter.toStoreUpdateResponseDTO(store);
    }

    /** 가게 삭제(소프트 삭제) */
    public void deleteStore(UUID storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getOwnerId().equals(userId)) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        store.softDelete();
        storeRepository.save(store);
    }
}
