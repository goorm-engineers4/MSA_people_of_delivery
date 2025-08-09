package com.example.cloudfour.userservice.domain.region.service;

import com.example.cloudfour.userservice.domain.region.entity.Region;
import com.example.cloudfour.userservice.domain.region.repository.RegionRepository;
import com.example.cloudfour.userservice.domain.region.util.RegionParser;
import com.example.cloudfour.userservice.domain.user.exception.UserAddressErrorCode;
import com.example.cloudfour.userservice.domain.user.exception.UserAddressException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    @Transactional(readOnly = true)
    public Region findOrCreate(String si, String gu, String dong) {
        return regionRepository.findAll().stream()
                .filter(r -> r.getSi().equals(si) && r.getGu().equals(gu) && r.getDong().equals(dong))
                .findFirst()
                .orElseGet(() -> regionRepository.save(Region.builder().si(si).gu(gu).dong(dong).build()));
    }

    @Transactional
    public Region resolveOrCreateByAddress(String fullAddress) {
        var parts = RegionParser.parse(fullAddress);
        if (parts == null) {
            throw new UserAddressException(UserAddressErrorCode.PARSE_FAILED);
        }
        return findOrCreate(parts.si(), parts.gu(), parts.dong());
    }
}


