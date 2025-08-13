package com.example.cloudfour.storeservice.domain.store.repository.querydsl;

import com.example.cloudfour.storeservice.domain.store.entity.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface StoreQueryDslRepository {
    Optional<Store> findByIdAndIsDeletedFalse(UUID storeId);

    Slice<Store> findAllByCategoryAndCursor(UUID categoryId, LocalDateTime cursor, Pageable pageable);

    Slice<Store> findAllByKeyWordAndRegion(String keyword, LocalDateTime cursor, Pageable pageable
            ,String siDo, String siGunGu, String eupMyeongDong);

    boolean existsByName(String name);
}
