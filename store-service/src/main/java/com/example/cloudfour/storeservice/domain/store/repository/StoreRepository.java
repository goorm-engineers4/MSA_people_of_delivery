package com.example.cloudfour.storeservice.domain.store.repository;

import com.example.cloudfour.storeservice.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID>{
    void deleteAllByCreatedAtBefore(LocalDateTime createdAtBefore);
    Optional<Store> findByIdAndIsDeletedFalse(UUID storeId);
    Boolean existsByNameAndIsDeletedFalse(String name);
}
