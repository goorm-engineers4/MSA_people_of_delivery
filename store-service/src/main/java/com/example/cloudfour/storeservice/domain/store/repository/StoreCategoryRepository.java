package com.example.cloudfour.storeservice.domain.store.repository;

import com.example.cloudfour.storeservice.domain.store.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {

    Optional<StoreCategory> findByCategory(String category);
}
