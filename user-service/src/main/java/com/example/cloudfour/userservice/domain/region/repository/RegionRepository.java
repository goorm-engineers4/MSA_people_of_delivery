package com.example.cloudfour.userservice.domain.region.repository;

import com.example.cloudfour.userservice.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
    Optional<Region> findBySiAndGuAndDong(String si, String gu, String dong);
}
