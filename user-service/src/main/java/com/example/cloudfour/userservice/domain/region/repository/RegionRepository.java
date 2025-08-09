package com.example.cloudfour.userservice.domain.region.repository;

import com.example.cloudfour.userservice.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {

    @Query("select r from Region r where r.si = :si and r.gu = :gu and r.dong = :dong")
    Optional<Region> findBySiAndGuAndDong(@Param("si") String si, @Param("gu") String gu, @Param("dong") String dong);
}
