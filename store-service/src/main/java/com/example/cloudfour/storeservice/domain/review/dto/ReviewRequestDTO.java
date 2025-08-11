package com.example.cloudfour.storeservice.domain.review.dto;

import com.example.cloudfour.storeservice.domain.review.controller.ReviewCommonRequestDTO;
import lombok.Builder;
import lombok.Getter;


public class ReviewRequestDTO {
    @Getter
    @Builder
    public static class ReviewCreateRequestDTO{
        ReviewCommonRequestDTO reviewCommonRequestDTO;
    }

    @Getter
    @Builder
    public static class ReviewUpdateRequestDTO{
        ReviewCommonRequestDTO reviewCommonRequestDTO;
    }
}
