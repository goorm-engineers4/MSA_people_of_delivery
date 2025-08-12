package com.example.cloudfour.storeservice.domain.review.controller;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ReviewCommonRequestDTO {
    UUID storeId;
    Float score;
    String content;
    String pictureUrl;
}
