package com.example.cloudfour.authservice.domain.auth.dto;

import java.util.UUID;

public class UserResposneDTO {
    public record UserBriefResponseDTO(
            UUID id,
            String email,
            String role,
            boolean emailVerified
    ) {}

    public record PasswordVerifyResponseDTO(boolean match) {}

    public record ExistsByEmailResponseDTO(boolean exists) {}
}
