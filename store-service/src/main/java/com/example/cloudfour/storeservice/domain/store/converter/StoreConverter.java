package com.example.cloudfour.storeservice.domain.store.converter;

import com.example.cloudfour.storeservice.domain.store.controller.StoreCommonResponseDTO;
import com.example.cloudfour.storeservice.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.modulecommon.entity.BaseEntity;
import java.time.LocalDateTime;
import java.util.List;

public class StoreConverter {

    public static Store toStore(StoreRequestDTO.StoreCreateRequestDTO dto) {
        return Store.builder()
                .name(dto.getStoreCommonRequestDTO().getName())
                .address(dto.getStoreCommonRequestDTO().getAddress())
                .storePicture(dto.getStorePicture())
                .phone(dto.getPhone())
                .content(dto.getContent())
                .minPrice(dto.getMinPrice())
                .deliveryTip(dto.getDeliveryTip())
                .operationHours(dto.getOperationHours())
                .closedDays(dto.getClosedDays())
                .rating(0f)
                .likeCount(0)
                .reviewCount(0)
                .build();
    }

    public static StoreResponseDTO.StoreCreateResponseDTO toStoreCreateResponseDTO(Store store) {
        return StoreResponseDTO.StoreCreateResponseDTO.builder()
                .storeCommonsBaseResponseDTO(toStoreCommonsBaseResponseDTO(store))
                .storeCommonMainResponseDTO(toStoreCommonMainResponseDTO(store))
                .createdAt(store.getCreatedAt())
                .createdBy(store.getOwnerId())
                .build();
    }

    public static StoreResponseDTO.StoreUpdateResponseDTO toStoreUpdateResponseDTO(Store store){
        return StoreResponseDTO.StoreUpdateResponseDTO.builder()
                .category(store.getStoreCategory().getCategory())
                .updatedAt(store.getUpdatedAt())
                .build();
    }

    public static StoreResponseDTO.StoreListResponseDTO toStoreListResponseDTO(Store store) {
        return StoreResponseDTO.StoreListResponseDTO.builder()
                .storeCommonsBaseResponseDTO(toStoreCommonsBaseResponseDTO(store))
                .storeCommonOptionResponseDTO(toStoreCommonOptionResponseDTO(store))
                .createdAt(store.getCreatedAt())
                .build();
    }

    public static StoreResponseDTO.StoreCursorListResponseDTO toStoreCursorListResponseDTO(
            List<StoreResponseDTO.StoreListResponseDTO> storeList,
            LocalDateTime nextCursor
    ) {
        return StoreResponseDTO.StoreCursorListResponseDTO.builder()
                .storeList(storeList)
                .nextCursor(nextCursor)
                .build();
    }


    public static StoreResponseDTO.StoreDetailResponseDTO toStoreDetailResponseDTO(Store store) {
        return StoreResponseDTO.StoreDetailResponseDTO.builder()
                .storeCommonsBaseResponseDTO(toStoreCommonsBaseResponseDTO(store))
                .storeCommonMainResponseDTO(toStoreCommonMainResponseDTO(store))
                .storeCommonOptionResponseDTO(toStoreCommonOptionResponseDTO(store))
                .build();
    }

    public static StoreCommonResponseDTO.StoreCommonsBaseResponseDTO toStoreCommonsBaseResponseDTO(Store store) {
        return StoreCommonResponseDTO.StoreCommonsBaseResponseDTO.builder()
                .storeId(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .storePicture(store.getStorePicture())
                .build();
    }

    public static StoreCommonResponseDTO.StoreCommonOptionResponseDTO toStoreCommonOptionResponseDTO(Store store) {
        return StoreCommonResponseDTO.StoreCommonOptionResponseDTO.builder()
                .rating(store.getRating())
                .reviewCount(store.getReviewCount())
                .build();
    }


    public static StoreCommonResponseDTO.StoreCommonMainResponseDTO toStoreCommonMainResponseDTO (Store store) {
        return StoreCommonResponseDTO.StoreCommonMainResponseDTO.builder()
                .phone(store.getPhone())
                .content(store.getContent())
                .minPrice(store.getMinPrice())
                .deliveryTip(store.getDeliveryTip())
                .operationHours(store.getOperationHours())
                .closedDays(store.getClosedDays())
                .category(store.getStoreCategory().getCategory())
                .build();
    }

}
