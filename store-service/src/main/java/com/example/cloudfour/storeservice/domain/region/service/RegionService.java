package com.example.cloudfour.storeservice.domain.region.service;

import com.example.cloudfour.storeservice.domain.region.entity.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final com.example.cloudfour.storeservice.domain.region.repository.RegionRepository regionRepository;

    public Long parseAndSaveRegion(String address) {
        String[] parts = address.trim().split("\\s+");

        if (parts.length < 3) {
            throw new IllegalArgumentException("주소 형식이 올바르지 않습니다: " + address);
        }

        String siDo = parts[0];        // "서울특별시", "경기도" 등
        String siGunGu = parts[1];     // "강서구", "파주시" 등
        String eupMyeonDong = parts[2]; // "화곡동", "와동동" 등

        return regionRepository.findBySiDoAndSiGunGuAndEupMyeonDong(siDo, siGunGu, eupMyeonDong)
                .map(Region::getId)
                .orElseGet(() -> {
                    Region newRegion = Region.builder()
                            .siDo(siDo)
                            .siGunGu(siGunGu)
                            .eupMyeonDong(eupMyeonDong)
                            .build();
                    return regionRepository.save(newRegion).getId();
                });
    }
}
