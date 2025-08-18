package com.example.cloudfour.storeservice.domain.store.service.query;

import com.example.cloudfour.storeservice.config.GatewayPrincipal;
import com.example.cloudfour.storeservice.domain.collection.document.StoreDocument;
import com.example.cloudfour.storeservice.domain.collection.repository.StoreSearchRepository;
import com.example.cloudfour.storeservice.domain.store.converter.StoreConverter;
import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
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

    private final StoreSearchRepository storeMongoRepository;

    public StoreResponseDTO.StoreCursorListResponseDTO getAllStores(
            LocalDateTime cursor, int size, String keyword,GatewayPrincipal user
    ) {
        if(user==null){
            log.warn("가게 목록 조회 권한 없음");
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("가게 검색 목록 조회 권한 확인 성공");
        String siDo = "서울특별시";
        String siGunGu = "서초구";
        String eupMyeongDong = "양재동";
        if(user.userId()==null){
            siDo = "서울특별시";
            siGunGu = "서초구";
            eupMyeongDong = "양재동";
        }
        //else{
            //userid를 통해 userRegion 정보 가져오기, 시군구 정보 입력해서 storeRegion이랑 비교
        //}
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, size);
        Slice<StoreDocument> storeSlice = storeMongoRepository.findAllStoreByKeyWordAndRegion(keyword, baseTime, pageable,siDo,siGunGu,eupMyeongDong);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreConverter::toStoreListResponseDTO)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;
        log.info("가게 검색 목록 조회 성공");
        return StoreConverter.toStoreCursorListResponseDTO(storeList, nextCursor);

    }
    public StoreResponseDTO.StoreCursorListResponseDTO getStoresByCategory(
            UUID categoryId, LocalDateTime cursor, int size,GatewayPrincipal user
    ) {
        if(user==null){
            log.warn("카테고리 별 가게 목록 조회 권한 없음");
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("가게 카테고리 별 목록 조회 확인 성공");
        LocalDateTime baseTime = (cursor != null) ? cursor : LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, size);
        Slice<StoreDocument> storeSlice = storeMongoRepository.findAllStoreByCategoryAndCursor(categoryId, baseTime, pageable);

        List<StoreResponseDTO.StoreListResponseDTO> storeList = storeSlice.getContent().stream()
                .map(StoreConverter::toStoreListResponseDTO)
                .toList();

        LocalDateTime nextCursor = storeSlice.hasNext() && !storeList.isEmpty()
                ? storeList.get(storeList.size() - 1).getCreatedAt()
                : null;
        log.info("가게 카테고리 별 목록 조회 성공");
        return StoreResponseDTO.StoreCursorListResponseDTO.of(storeList, nextCursor);
    }

    public StoreResponseDTO.StoreDetailResponseDTO getStoreById(UUID storeId,GatewayPrincipal user) {
        if(user==null){
            log.warn("가게 상세 조회 권한 없음");
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("가게 상세 조회 권한 확인 성공");
        StoreDocument store = storeMongoRepository.findStoreByStoreId(storeId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 가게");
                    return new StoreException(StoreErrorCode.NOT_FOUND);
                });
        log.info("가게 상제 조회 성공");
        return StoreConverter.toStoreDetailResponseDTO(store);
    }
}
