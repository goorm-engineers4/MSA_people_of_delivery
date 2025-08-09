package com.example.cloudfour.storeservice.domain.store.controller;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoreCommonRequestDTO {
    private String name;
    private String address;
    private String category;
}
