package com.example.cloudfour.authservice.domain.auth.converter;

import com.example.cloudfour.authservice.domain.auth.dto.AuthModelDTO;
import com.example.cloudfour.authservice.domain.auth.dto.AuthResponseDTO;
import com.example.cloudfour.authservice.domain.auth.dto.RefreshDTO;
import com.example.cloudfour.authservice.domain.auth.dto.TokenDTO;
import com.example.cloudfour.authservice.domain.auth.entity.VerificationCode;
import com.example.cloudfour.authservice.domain.auth.enums.VerificationPurpose;

import java.time.LocalDateTime;

public class AuthConverter {

    public static AuthResponseDTO.AuthRegisterResponseDTO toAuthRegisterResponseDTO(AuthModelDTO.RegisterResultDTO m) {
        return AuthResponseDTO.AuthRegisterResponseDTO.builder()
                .userId(m.userId())
                .email(m.email())
                .nickname(m.nickname())
                .role(m.role())
                .build();
    }

    public static AuthResponseDTO.AuthTokenResponseDTO toAuthTokenResponseDTO(TokenDTO token) {
        return AuthResponseDTO.AuthTokenResponseDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }

    public static AuthResponseDTO.AuthRefreshTokenResponseDTO toAuthRefreshTokenResponseDTO(RefreshDTO token) {
        return AuthResponseDTO.AuthRefreshTokenResponseDTO.builder()
                .accessToken(token.getAccessToken())
                .accessTokenExpiresIn(token.getAccessTokenExpiresIn())
                .build();
    }

    public static VerificationCode toVerificationCode(String email, String code, LocalDateTime expiry, VerificationPurpose purpose) {
        return VerificationCode.builder()
                .email(email)
                .code(code)
                .expiryDate(expiry)
                .purpose(purpose)
                .build();
    }
}


