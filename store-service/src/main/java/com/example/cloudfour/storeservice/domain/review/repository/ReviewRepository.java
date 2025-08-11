package com.example.cloudfour.storeservice.domain.review.repository;

import com.example.cloudfour.storeservice.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query("select r from Review r where r.isDeleted = false and r.user =:UserId and r.userIsDeleted = false and r.createdAt <:cursor order by r.createdAt desc")
    Slice<Review> findAllByUserId(@Param("UserId") UUID userId, LocalDateTime cursor, Pageable pageable);

    @Query("select r from Review r where r.isDeleted = false and r.store =:StoreId and r.storeIsDeleted = false and r.createdAt <:cursor order by r.createdAt desc")
    Slice<Review> findAllByStoreId(@Param("StoreId") UUID storeId, LocalDateTime cursor, Pageable pageable);

    @Query("select count(r) > 0 from Review r where r.id =:ReviewId and r.user =:UserId and r.userIsDeleted = false")
    boolean existsByReviewIdAndUserId(@Param("ReviewId") UUID reviewId, @Param("UserId") UUID userId);
}
