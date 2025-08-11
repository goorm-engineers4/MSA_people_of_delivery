package com.example.cloudfour.storeservice.domain.review.dto;

import com.example.cloudfour.storeservice.domain.review.controller.ReviewCommonResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReviewResponseDTO {
    @Getter
    @Builder
    public static class ReviewCreateResponseDTO{
        UUID storeId;
        UUID reviewId;
        ReviewCommonResponseDTO.ReviewCommonCrudResponseDTO reviewCommonCrudResponseDTO;
        LocalDateTime createdAt;
        UUID createdBy;
    }

    @Getter
    @Builder
    public static class ReviewUpdateResponseDTO{
        UUID storeId;
        ReviewCommonResponseDTO.ReviewCommonCrudResponseDTO reviewCommonCrudResponseDTO;
        LocalDateTime updatedAt;
        UUID updatedBy;
    }

    @Getter
    @Builder
    public static class ReviewStoreResponseDTO{
        UUID reviewId;
        ReviewCommonResponseDTO.ReviewCommonCrudResponseDTO reviewCommonCrudResponseDTO;
        LocalDateTime createdAt;
        UUID createdBy;
    }

    @Getter
    @Builder
    public static class ReviewStoreListResponseDTO{
        List<ReviewStoreResponseDTO> reviews;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class ReviewUserResponseDTO{
        ReviewCommonResponseDTO.ReviewCommonGetResponseDTO reviewCommonGetResponseDTO;
        LocalDateTime createdAt;
        UUID createdBy;
    }

    @Getter
    @Builder
    public static class ReviewUserListResponseDTO{
        List<ReviewUserResponseDTO> reviews;
        private boolean hasNext;
        private LocalDateTime cursor;
    }

    @Getter
    @Builder
    public static class ReviewDetailResponseDTO{
        UUID storeId;
        UUID userId;
        String nickname;
        ReviewCommonResponseDTO.ReviewCommonGetResponseDTO reviewCommonGetResponseDTO;
        LocalDateTime createdAt;
    }
}
