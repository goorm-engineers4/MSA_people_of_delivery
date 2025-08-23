package com.example.cloudfour.paymentservice.domain.payment.apiclient;

import com.example.cloudfour.paymentservice.commondto.StoreResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreClient {

    private final RestTemplate restTemplate;
    private static final String BASE = "http://store-service/internal/stores";

    public Boolean existStore(UUID storeId) {
        if (storeId == null) {
            log.warn("Store ID가 null입니다");
            return false;
        }

        try {
            restTemplate.headForHeaders(BASE + "/exists?storeId=" + storeId);
            log.info("스토어 존재 확인 완료: {}", storeId);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            log.info("스토어가 존재하지 않음: {}", storeId);
            return false;
        } catch (Exception e) {
            log.error("스토어 존재 여부 확인 실패: {}", storeId, e);
            throw e;
        }
    }

    public StoreResponseDTO getStoreById(UUID storeId) {
        if (storeId == null) {
            log.warn("Store ID가 null입니다");
            return null;
        }

        try {
            StoreResponseDTO store = restTemplate.getForObject(BASE + "/{storeId}", StoreResponseDTO.class, storeId);
            log.info("스토어 정보 조회 완료: {}", storeId);
            return store;
        } catch (Exception e) {
            log.error("스토어 정보 조회 실패: {}", storeId, e);
            throw e;
        }
    }
}
