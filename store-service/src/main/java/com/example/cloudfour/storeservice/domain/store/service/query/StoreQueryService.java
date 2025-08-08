package com.example.cloudfour.storeservice.domain.store.service.query;

import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreQueryService {

    private final StoreRepository storeRepository;

    /** 전체 가게 목록 조회 */
    public StoreResponseDTO.StoreCursorListResponseDTO getAllStores(
            LocalDateTime cursor, int size, String keyword, UUID userId
    ) {
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByKeyWord(keyword, baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    /** 카테고리별 가게 목록 조회 */
    public StoreResponseDTO.StoreCursorListResponseDTO getStoresByCategory(
            UUID categoryId, LocalDateTime cursor, int size, UUID userId
    ) {
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByCategoryAndCursor(categoryId, baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreResponseDTO.StoreListResponseDTO::from)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    /** 단일 가게 조회 */
    public StoreResponseDTO.StoreDetailResponseDTO getStoreById(UUID storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        return StoreResponseDTO.StoreDetailResponseDTO.from(store);
    }
}
