package com.example.cloudfour.cartservice.client;

import com.example.cloudfour.cartservice.commondto.UserAddressResponseDTO;
import com.example.cloudfour.cartservice.commondto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate rt;

    private static final String BASE = "http://user-service/internal/users";

    public UserAddressResponseDTO addressById(UUID userid) {
        return rt.getForObject(BASE + "/{userId}", UserAddressResponseDTO.class, userid);
    }

    public UserResponseDTO userById(UUID userid){
        return rt.getForObject(BASE + "/{userId}", UserResponseDTO.class, userid);
    }


}
