package com.example.cloudfour.storeservice.domain.store.controller;

import com.example.cloudfour.modulecommon.apiPayLoad.CustomResponse;
import com.example.cloudfour.storeservice.domain.store.converter.StoreConverter;
import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/stores")
public class InternalStoreController {
    private final StoreRepository query;

    @GetMapping("/{storeId}")
    public CustomResponse<StoreResponseDTO.StoreDetailResponseDTO> getStoreDetail(
            @PathVariable UUID storeId
    ) {
        Store findStore = query.findByIdAndIsDeletedFalse(storeId).orElseThrow(
                ()->new StoreException(StoreErrorCode.NOT_FOUND));
        StoreResponseDTO.StoreDetailResponseDTO result = StoreConverter.toStoreDetailResponseDTO(findStore);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

}
