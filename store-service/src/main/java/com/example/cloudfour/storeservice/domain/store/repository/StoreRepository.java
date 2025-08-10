package com.example.cloudfour.storeservice.domain.store.repository;

import com.example.cloudfour.storeservice.domain.store.entity.Store;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    @Query("SELECT s FROM Store s WHERE s.ownerId = :userId AND s.user.isDeleted = false")
    Optional<Store> findByUserId(@Param("userId") UUID userId);

    boolean existsByName(String name);

    Optional<Store> findByIdAndIsDeletedFalse(UUID storeId);

    @Query("select count(s) > 0 from Store s where s.ownerId = :userId and s.id = :storeId and s.user.isDeleted = false")
    boolean existsByStoreAndUser(@Param("storeId") UUID storeId, @Param("userId") UUID userId);

    @Query("SELECT s FROM Store s WHERE s.isDeleted = false AND s.storeCategory.id = :categoryId AND s.createdAt < :cursor ORDER BY s.createdAt DESC")
    Slice<Store> findAllByCategoryAndCursor(@Param("categoryId") UUID categoryId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    @Query("select s from Store s where s.isDeleted = false " +
            "and (s.name ilike concat('%', :keyword, '%')" +
            "or s.storeCategory.category ilike concat('%', :keyword, '%'))" +
            "and s.createdAt < :cursor ORDER BY s.createdAt DESC")
    Slice<Store> findAllByKeyWord(@Param("keyword") String keyword, LocalDateTime cursor, Pageable pageable);
}
