package com.example.cloudfour.userservice.domain.region.service;

import com.example.cloudfour.userservice.domain.region.entity.Region;
import com.example.cloudfour.userservice.domain.region.exception.RegionErrorCode;
import com.example.cloudfour.userservice.domain.region.exception.RegionException;
import com.example.cloudfour.userservice.domain.region.repository.RegionRepository;
import com.example.cloudfour.userservice.domain.region.util.RegionParser;
import com.example.cloudfour.userservice.domain.user.exception.UserAddressErrorCode;
import com.example.cloudfour.userservice.domain.user.exception.UserAddressException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    @Transactional
    public Region getOrCreateFromAddress(String fullAddress) {
        var p = RegionParser.parseOrThrow(fullAddress);
        var n = normalize(p.si(), p.gu(), p.dong());
        return getOrCreate(n.si, n.gu, n.dong);
    }

    @Transactional
    public Region getOrCreate(String si, String gu, String dong) {
        var n = normalize(si, gu, dong);
        return regionRepository.findBySiAndGuAndDong(n.si, n.gu, n.dong)
                .orElseGet(() -> insertOrFind(n.si, n.gu, n.dong));
    }

    private Region insertOrFind(String si, String gu, String dong) {
        try {
            Region saved = regionRepository.save(Region.ofRaw(si, gu, dong));
            regionRepository.flush();
            return saved;
        } catch (DataIntegrityViolationException e) {
            return regionRepository.findBySiAndGuAndDong(si, gu, dong)
                    .orElseThrow(() -> e);
        }
    }

    private static record Sgd(String si, String gu, String dong) {}

    private static Sgd normalize(String si, String gu, String dong) {
        return new Sgd(nn(si), nn(gu), nn(dong));
    }
    private static String nn(String v) {
        if (v == null) throw new RegionException(RegionErrorCode.INTERNAL_ERROR);
        String out = v.trim().replaceAll("\\s+", " ");
        if (out.isEmpty()) throw new RegionException(RegionErrorCode.INTERNAL_ERROR);
        return out;
    }
}


