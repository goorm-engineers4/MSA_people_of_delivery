package com.example.cloudfour.paymentservice.commondto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private UUID userId;
    private String email;
    private String name;
    private String phoneNumber;
    private boolean isDeleted;
}
