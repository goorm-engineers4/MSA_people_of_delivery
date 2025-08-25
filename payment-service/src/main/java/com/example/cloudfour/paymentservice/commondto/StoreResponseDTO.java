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
public class StoreResponseDTO {
    private UUID storeId;
    private String storeName;
    private String address;
    private String phoneNumber;
    private boolean isDeleted;
}
