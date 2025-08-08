package com.example.cloudfour.storeservice.domain.store.converter;

import com.example.cloudfour.storeservice.domain.store.controller.StoreCommonResponseDTO;
import com.example.cloudfour.storeservice.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.entity.Store;

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
                .storeId(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .storePicture(store.getStorePicture())
                .storeCommonMainResponseDTO(toStoreCommonMainResponseDTO(store))
                .createdAt(store.getCreatedAt())
                .createdBy(store.getUser().getId())
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

    public static StoreResponseDTO.StoreUpdateResponseDTO toStoreUpdateResponseDTO(Store store){
        return StoreResponseDTO.StoreUpdateResponseDTO.builder()
                .category(store.getStoreCategory().getCategory())
                .updatedAt(store.getUpdatedAt())
                .build();
    }
}
