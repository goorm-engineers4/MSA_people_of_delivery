package com.example.cloudfour.storeservice.domain.menu.repository;

import com.example.cloudfour.storeservice.domain.menu.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {

    @Query("SELECT mc FROM MenuCategory mc WHERE mc.category = :category")
    Optional<MenuCategory> findByCategory(@Param("category") String category);
}
