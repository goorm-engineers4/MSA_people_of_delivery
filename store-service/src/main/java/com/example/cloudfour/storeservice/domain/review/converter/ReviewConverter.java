package com.example.cloudfour.storeservice.domain.review.converter;

import com.example.cloudfour.storeservice.domain.review.controller.ReviewCommonResponseDTO;
import com.example.cloudfour.storeservice.domain.review.dto.ReviewRequestDTO;
import com.example.cloudfour.storeservice.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.storeservice.domain.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewConverter {
    public static Review toReview(ReviewRequestDTO.ReviewCreateRequestDTO reviewCreateRequestDTO){
        return Review.builder()
                .score(reviewCreateRequestDTO.getReviewCommonRequestDTO().getScore())
                .content(reviewCreateRequestDTO.getReviewCommonRequestDTO().getContent())
                .pictureUrl(reviewCreateRequestDTO.getReviewCommonRequestDTO().getPictureUrl())
                .build();
    }

    public static ReviewResponseDTO.ReviewDetailResponseDTO toReviewDetailResponseDTO(Review review, String nickname){
        return ReviewResponseDTO.ReviewDetailResponseDTO.builder()
                .storeId(review.getStore().getId())
                .userId(review.getUser())
                .nickname(nickname)
                .reviewCommonGetResponseDTO(toReviewCommonGetResponseDTO(review))
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static ReviewResponseDTO.ReviewStoreResponseDTO toReviewStoreResponseDTO(Review review){
        return ReviewResponseDTO.ReviewStoreResponseDTO.builder()
                .reviewId(review.getId())
                .reviewCommonCrudResponseDTO(toReviewCommonCrudResponseDTO(review))
                .createdAt(review.getCreatedAt())
                .createdBy(review.getUser())
                .build();
    }

    public static ReviewResponseDTO.ReviewStoreListResponseDTO toReviewStoreListResponseDTO(List<ReviewResponseDTO.ReviewStoreResponseDTO> reviews, Boolean hasNext, LocalDateTime cursor) {
        return ReviewResponseDTO.ReviewStoreListResponseDTO.builder()
                .reviews(reviews)
                .hasNext(hasNext)
                .cursor(cursor)
                .build();
    }

    public static ReviewResponseDTO.ReviewUserResponseDTO toReviewUserResponseDTO(Review review){
        return ReviewResponseDTO.ReviewUserResponseDTO.builder()
                .reviewCommonGetResponseDTO(toReviewCommonGetResponseDTO(review))
                .createdAt(review.getCreatedAt())
                .createdBy(review.getUser())
                .build();
    }

    public static ReviewResponseDTO.ReviewUserListResponseDTO toReviewUserListResponseDTO(List<ReviewResponseDTO.ReviewUserResponseDTO> reviews, Boolean hasNext, LocalDateTime cursor) {
        return ReviewResponseDTO.ReviewUserListResponseDTO.builder()
                .reviews(reviews)
                .hasNext(hasNext)
                .cursor(cursor)
                .build();
    }

    public static ReviewResponseDTO.ReviewCreateResponseDTO toReviewCreateResponseDTO(Review review){
        return ReviewResponseDTO.ReviewCreateResponseDTO.builder()
                .reviewId(review.getId())
                .storeId(review.getStore().getId())
                .reviewCommonCrudResponseDTO(toReviewCommonCrudResponseDTO(review))
                .createdAt(review.getCreatedAt())
                .createdBy(review.getUser())
                .build();
    }

    public static ReviewResponseDTO.ReviewUpdateResponseDTO toReviewUpdateResponseDTO(Review review){
        return ReviewResponseDTO.ReviewUpdateResponseDTO.builder()
                .storeId(review.getStore().getId())
                .reviewCommonCrudResponseDTO(toReviewCommonCrudResponseDTO(review))
                .updatedAt(review.getUpdatedAt())
                .updatedBy(review.getUser())
                .build();
    }

    public static ReviewCommonResponseDTO.ReviewCommonGetResponseDTO toReviewCommonGetResponseDTO(Review review){
        return ReviewCommonResponseDTO.ReviewCommonGetResponseDTO.builder()
                .reviewId(review.getId())
                .score(review.getScore())
                .content(review.getContent())
                .pictureUrl(review.getPictureUrl())
                .build();
    }

    public static ReviewCommonResponseDTO.ReviewCommonCrudResponseDTO toReviewCommonCrudResponseDTO(Review review){
        return ReviewCommonResponseDTO.ReviewCommonCrudResponseDTO.builder()
                .userId(review.getUser())
                .score(review.getScore())
                .content(review.getContent())
                .pictureUrl(review.getPictureUrl())
                .build();
    }
}

