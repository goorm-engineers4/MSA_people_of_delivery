package com.example.cloudfour.userservice.domain.auth.controller;

import com.example.cloudfour.userservice.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthCommonResponseDTO {
    private final String email;
    private final String nickname;
    private final String encodedPassword;
    private final String number;
    private final Role role;
}


