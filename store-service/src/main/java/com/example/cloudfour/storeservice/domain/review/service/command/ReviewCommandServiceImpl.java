package com.example.cloudfour.storeservice.domain.review.service.command;

import com.example.cloudfour.storeservice.config.GatewayPrincipal;
import com.example.cloudfour.storeservice.domain.review.converter.ReviewConverter;
import com.example.cloudfour.storeservice.domain.review.dto.ReviewRequestDTO;
import com.example.cloudfour.storeservice.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.storeservice.domain.review.entity.Review;
import com.example.cloudfour.storeservice.domain.review.exception.ReviewErrorCode;
import com.example.cloudfour.storeservice.domain.review.exception.ReviewException;
import com.example.cloudfour.storeservice.domain.review.repository.ReviewRepository;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandServiceImpl{
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    public ReviewResponseDTO.ReviewCreateResponseDTO createReview(ReviewRequestDTO.ReviewCreateRequestDTO reviewCreateRequestDTO, GatewayPrincipal user) {
        Store findStore = storeRepository.findById(reviewCreateRequestDTO.getReviewCommonRequestDTO().getStoreId()).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        Review review = ReviewConverter.toReview(reviewCreateRequestDTO);
        review.setUser(user.userId());
        review.setStore(findStore);
        reviewRepository.save(review);
        return ReviewConverter.toReviewCreateResponseDTO(review);
    }

    public void deleteReview(UUID reviewId, GatewayPrincipal user) {
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new ReviewException(ReviewErrorCode.NOT_FOUND));
        if(!reviewRepository.existsByReviewIdAndUserId(reviewId, user.userId())) {
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_ACCESS);
        }
        findReview.softDelete();
    }

    public ReviewResponseDTO.ReviewUpdateResponseDTO updateReview(ReviewRequestDTO.ReviewUpdateRequestDTO reviewUpdateRequestDTO, UUID reviewId, GatewayPrincipal user) {
        Store findStore = storeRepository.findById(reviewUpdateRequestDTO.getReviewCommonRequestDTO().getStoreId()).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new ReviewException(ReviewErrorCode.NOT_FOUND));
        if(!reviewRepository.existsByReviewIdAndUserId(reviewId, user.userId())) {
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_ACCESS);
        }
        findReview.update(reviewUpdateRequestDTO);
        findReview.setStore(findStore);
        return ReviewConverter.toReviewUpdateResponseDTO(findReview);
    }
}
