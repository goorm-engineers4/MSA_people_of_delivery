package com.example.cloudfour.userservice.domain.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequestDTO {

    public record RegisterRequestDto(
            @Email @NotBlank String email,
            @NotBlank @Size(min=2, max=20) String nickname,
            @NotBlank @Size(min=8, max=64) String password,
            @NotBlank String number
    ) {}

    public record LoginRequestDto(
            @Email @NotBlank String email,
            @NotBlank @Size(min=8, max=64) String password
    ) {}

    public record PasswordChangeDto(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 8, max = 64) String newPassword
    ) {}


    public record EmailVerifyRequestDTO(
            @Email @NotBlank String email,
            @NotBlank @Size(min=6, max=6) String code
    ) {}

    public record EmailChangeStartRequest(
            @Email @NotBlank String newEmail
    ) {}

    public record EmailChangeVerifyRequest(
            @Email @NotBlank String newEmail,
            @NotBlank @Size(min=6, max=6) String code
    ) {}
}

