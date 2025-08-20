package com.example.cloudfour.storeservice.domain.review.dto;

import com.example.cloudfour.storeservice.domain.review.controller.ReviewCommonRequestDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


public class ReviewRequestDTO {
    @Getter
    @Builder
    public static class ReviewCreateRequestDTO{
        @JsonUnwrapped
        ReviewCommonRequestDTO reviewCommonRequestDTO;
    }

    @Getter
    @Builder
    public static class ReviewUpdateRequestDTO{
        @JsonUnwrapped
        ReviewCommonRequestDTO reviewCommonRequestDTO;
    }

    @Getter
    @Builder
    public static class testRequestDTO{
        UUID reviewId;
        UUID userId;
        UUID storeId;
        String userName;
        Float score;
        String content;
        String pictureUrl;
        LocalDateTime createdAt;
    }
}
