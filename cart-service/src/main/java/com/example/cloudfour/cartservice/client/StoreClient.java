package com.example.cloudfour.cartservice.client;

import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
import com.example.cloudfour.cartservice.commondto.MenuResponseDTO;
import com.example.cloudfour.cartservice.commondto.StoreResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreClient {
    private final RestTemplate rt;

    private static final String BASE = "http://store-service/internal";

    public Boolean existStore(UUID storeId) {
        try {
            rt.headForHeaders(BASE + "/stores/exists?storeId=" + storeId);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    public Boolean existMenu(UUID menuId) {
        try {
            rt.headForHeaders(BASE + "/menus/exists?menuId=" + menuId);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    public StoreResponseDTO storeById(UUID storeId) {
        return rt.getForObject(BASE + "/stores/{storeId}", StoreResponseDTO.class, storeId);
    }

    public MenuResponseDTO menuById(UUID menuId) {
        return rt.getForObject(BASE +"/menus/{menuId}", MenuResponseDTO.class,menuId);
    }

    public MenuOptionResponseDTO menuOptionById(UUID menuOptionId) {
        return rt.getForObject(BASE +"/menus/options/{optionId}/detail", MenuOptionResponseDTO.class, menuOptionId);
    }

    public MenuOptionResponseDTO menuOptionsById(UUID menuOptionId) {
        return rt.getForObject(BASE +"/menus/options/{optionId}/detail", MenuOptionResponseDTO.class, menuOptionId);
    }

    public List<MenuOptionResponseDTO> menuOptionsByIds(List<UUID> menuOptionIds) {
        if (menuOptionIds == null || menuOptionIds.isEmpty()) {
            return List.of();
        }

        return menuOptionIds.stream()
                .map(id -> {
                    try {
                        return menuOptionById(id);
                    } catch (Exception e) {
                        System.err.println("Failed to fetch menu option: " + id + ", error: " + e.getMessage());
                        return null;
                    }
                })
                .filter(option -> option != null)
                .toList();
    }


}
