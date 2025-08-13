package com.example.cloudfour.storeservice.domain.store.service.query;

import com.example.cloudfour.storeservice.config.GatewayPrincipal;
import com.example.cloudfour.storeservice.domain.store.converter.StoreConverter;
import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreQueryService {

    private final StoreRepository storeRepository;

    public StoreResponseDTO.StoreCursorListResponseDTO getAllStores(
            LocalDateTime cursor, int size, String keyword,GatewayPrincipal user
    ) {
        if(user==null){
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        String siDo = "서울특별시";
        String siGunGu = "종로구";
        String eupMyeongDong = "사직동";
        if(user.userId()==null){
            siDo = "서울특별시";
            siGunGu = "종로구";
            eupMyeongDong = "사직동";
        }
        //else{
            //userid를 통해 userRegion 정보 가져오기, 시군구 정보 입력해서 storeRegion이랑 비교
        //}
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByKeyWordAndRegion(keyword, baseTime, pageable,siDo,siGunGu,eupMyeongDong);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreConverter::toStoreListResponseDTO)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreConverter.toStoreCursorListResponseDTO(storeList, nextCursor);

    }
    public StoreResponseDTO.StoreCursorListResponseDTO getStoresByCategory(
            UUID categoryId, LocalDateTime cursor, int size,GatewayPrincipal user
    ) {
        if(user==null){
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, size);
        Slice<Store> storeSlice = storeRepository.findAllByCategoryAndCursor(categoryId, baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreConverter::toStoreListResponseDTO)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;

        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    public StoreResponseDTO.StoreDetailResponseDTO getStoreById(UUID storeId,GatewayPrincipal user) {
        if(user==null){
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));
        return StoreConverter.toStoreDetailResponseDTO(store);
    }
}
