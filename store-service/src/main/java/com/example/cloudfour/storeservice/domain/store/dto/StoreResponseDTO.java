package com.example.cloudfour.storeservice.domain.store.dto;

import com.example.cloudfour.storeservice.domain.store.controller.StoreCommonResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class StoreResponseDTO {

    @Getter
    @SuperBuilder
    public static abstract class StoreBaseResponseDTO {
        protected UUID storeId;
        protected String name;
        protected String address;
        protected String storePicture;
    }

    @Getter
    @SuperBuilder
    public static class StoreCreateResponseDTO extends StoreBaseResponseDTO {
        StoreCommonResponseDTO.StoreCommonMainResponseDTO storeCommonMainResponseDTO;
        private LocalDateTime createdAt;
        private UUID createdBy;
    }

    @Getter
    @SuperBuilder
    public static class StoreUpdateResponseDTO extends StoreBaseResponseDTO {
        private String category;
        private LocalDateTime updatedAt;
    }

    @Getter
    @SuperBuilder
    public static class StoreListResponseDTO extends StoreBaseResponseDTO {
        StoreCommonResponseDTO.StoreCommonOptionResponseDTO storeCommonOptionResponseDTO;

        private LocalDateTime createdAt;
    }

    @Getter
    @SuperBuilder
    public static class StoreDetailResponseDTO extends StoreBaseResponseDTO {
        StoreCommonResponseDTO.StoreCommonMainResponseDTO storeCommonMainResponseDTO;
        StoreCommonResponseDTO.StoreCommonOptionResponseDTO storeCommonOptionResponseDTO;
    }

    @Getter
    @Builder
    public static class StoreCursorListResponseDTO {
        private List<StoreListResponseDTO> storeList;
        private LocalDateTime nextCursor;

        public static StoreCursorListResponseDTO of(List<StoreListResponseDTO> storeList, LocalDateTime nextCursor) {
            return StoreCursorListResponseDTO.builder()
                    .storeList(storeList)
                    .nextCursor(nextCursor)
                    .build();
        }
    }
}
