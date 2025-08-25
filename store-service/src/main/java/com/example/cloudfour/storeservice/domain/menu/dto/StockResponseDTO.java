package com.example.cloudfour.storeservice.domain.menu.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockResponseDTO {
    private Long quantity;
}
