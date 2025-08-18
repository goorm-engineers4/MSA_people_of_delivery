package com.example.cloudfour.storeservice.domain.collection.repository;

import com.example.cloudfour.storeservice.domain.collection.document.ReviewDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ReviewMongoQueryRepository {
    Slice<ReviewDocument> findAllByUserId(UUID userId, LocalDateTime cursor, Pageable pageable);

    Slice<ReviewDocument> findAllByStoreId(UUID storeId, LocalDateTime cursor, Pageable pageable);

    Optional<ReviewDocument> findById(UUID reviewId);
}
