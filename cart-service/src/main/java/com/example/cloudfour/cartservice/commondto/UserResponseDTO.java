package com.example.cloudfour.cartservice.commondto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserResponseDTO {
    String nickname;
}
