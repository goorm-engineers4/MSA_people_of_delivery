package com.example.cloudfour.userservice.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class AuthResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AuthRegisterResponseDTO {
        private UUID userId;
        private String email;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AuthLoginResponseDTO {
        private UUID userId;
        private String role;
        private boolean emailVerified;
    }
}
