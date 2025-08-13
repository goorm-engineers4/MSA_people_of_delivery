package com.example.cloudfour.storeservice.domain.menu.repository.querydsl;

import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MenuQueryDslRepository {
    List<Menu> findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(UUID storeId);

    Slice<Menu> findByStoreIdAndDeletedFalseAndCreatedAtBefore(UUID storeId, LocalDateTime cursor, Pageable pageable);

    Slice<Menu> findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(UUID storeId, UUID menuCategoryId ,LocalDateTime cursor, Pageable pageable);

    boolean existsByNameAndStoreId(String name, UUID storeId);
}
