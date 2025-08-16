package com.example.cloudfour.storeservice.domain.store.repository;

import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.repository.querydsl.StoreQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID>, StoreQueryDslRepository {
    void deleteAllByCreatedAtBefore(LocalDateTime createdAtBefore);
}
