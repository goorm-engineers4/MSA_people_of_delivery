package com.example.cloudfour.userservice.domain.auth.converter;

import com.example.cloudfour.userservice.domain.auth.dto.AuthResponseDTO;
import com.example.cloudfour.userservice.domain.auth.controller.AuthCommonResponseDTO;
import com.example.cloudfour.userservice.domain.auth.dto.RefreshDTO;
import com.example.cloudfour.userservice.domain.auth.dto.TokenDTO;
import com.example.cloudfour.userservice.domain.auth.entity.VerificationCode;
import com.example.cloudfour.userservice.domain.auth.enums.VerificationPurpose;
import com.example.cloudfour.userservice.domain.user.entity.User;
import com.example.cloudfour.userservice.domain.user.enums.LoginType;

import java.time.LocalDateTime;

public class AuthConverter {

    public static AuthResponseDTO.AuthRegisterResponseDTO toAuthRegisterResponseDTO(User user) {
        return AuthResponseDTO.AuthRegisterResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
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

    public static User toUser(AuthCommonResponseDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(dto.getEncodedPassword())
                .number(dto.getNumber())
                .role(dto.getRole())
                .loginType(LoginType.LOCAL)
                .emailVerified(false)
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


