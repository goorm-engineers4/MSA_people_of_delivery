package com.example.cloudfour.storeservice.domain.region.service;

import com.example.cloudfour.storeservice.domain.region.entity.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final com.example.cloudfour.storeservice.domain.region.repository.RegionRepository regionRepository;

    public UUID parseAndSaveRegion(String address) {
        String[] parts = address.trim().split("\\s+");
        if (parts.length < 3) throw new IllegalArgumentException("주소 형식이 올바르지 않습니다: " + address);

        String siDo = parts[0], siGunGu = parts[1], eupMyeonDong = parts[2];

        return regionRepository.findBySiDoAndSiGunGuAndEupMyeonDong(siDo, siGunGu, eupMyeonDong)
                .map(Region::getId) // UUID
                .orElseGet(() -> {
                    Region newRegion = Region.builder()
                            .siDo(siDo).siGunGu(siGunGu).eupMyeonDong(eupMyeonDong)
                            .build();
                    return regionRepository.save(newRegion).getId(); // UUID
                });
    }
}
