package com.example.cloudfour.storeservice.domain.review.service.query;

import com.example.cloudfour.storeservice.config.GatewayPrincipal;
import com.example.cloudfour.storeservice.domain.commondto.UserResponseDTO;
import com.example.cloudfour.storeservice.domain.review.converter.ReviewConverter;
import com.example.cloudfour.storeservice.domain.review.dto.ReviewResponseDTO;
import com.example.cloudfour.storeservice.domain.review.entity.Review;
import com.example.cloudfour.storeservice.domain.review.exception.ReviewErrorCode;
import com.example.cloudfour.storeservice.domain.review.exception.ReviewException;
import com.example.cloudfour.storeservice.domain.review.repository.ReviewRepository;
import com.example.cloudfour.storeservice.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.storeservice.domain.store.exception.StoreException;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryServiceImpl{
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final RestTemplate restTemplate;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);

    public ReviewResponseDTO.ReviewDetailResponseDTO getReviewById(UUID reviewId, GatewayPrincipal user) {
        UserResponseDTO findUser = restTemplate.getForObject("http://localhost:8080/api/users/me", UserResponseDTO.class);
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->new ReviewException(ReviewErrorCode.NOT_FOUND));
        return ReviewConverter.toReviewDetailResponseDTO(findReview, findUser.getNickname());
    }

    public ReviewResponseDTO.ReviewStoreListResponseDTO getReviewListByStore(UUID storeId, LocalDateTime cursor, Integer size, GatewayPrincipal user) {
        storeRepository.findById(storeId).orElseThrow(()->new StoreException(StoreErrorCode.NOT_FOUND));
        if(cursor==null){
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0,size);

        Slice<Review> findReviews = reviewRepository.findAllByStoreId(storeId,cursor,pageable);
        if(findReviews.isEmpty()){
            throw new ReviewException(ReviewErrorCode.NOT_FOUND);
        }
        List<Review> reviews = findReviews.toList();
        List<ReviewResponseDTO.ReviewStoreResponseDTO> reviewStoreListResponseDTOS = reviews.stream().map(ReviewConverter::toReviewStoreResponseDTO).toList();
        LocalDateTime next_cursor = null;
        if(!findReviews.isEmpty() && findReviews.hasNext()) {
            next_cursor = reviews.getLast().getCreatedAt();
        }

        return ReviewConverter.toReviewStoreListResponseDTO(reviewStoreListResponseDTOS,findReviews.hasNext(),next_cursor);
    }

    public ReviewResponseDTO.ReviewUserListResponseDTO getReviewListByUser(LocalDateTime cursor, Integer size, GatewayPrincipal user) {
        if(cursor==null){
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0,size);
        Slice<Review> findReviews = reviewRepository.findAllByUserId(user.userId(),cursor,pageable);
        if(findReviews.isEmpty()){
            throw new ReviewException(ReviewErrorCode.NOT_FOUND);
        }
        List<Review> reviews = findReviews.toList();
        List<ReviewResponseDTO.ReviewUserResponseDTO> reviewUserListResponseDTOS = reviews.stream().map(ReviewConverter::toReviewUserResponseDTO).toList();
        LocalDateTime next_cursor = null;
        if(!findReviews.isEmpty() && findReviews.hasNext()) {
            next_cursor = reviews.getLast().getCreatedAt();
        }
        return ReviewConverter.toReviewUserListResponseDTO(reviewUserListResponseDTOS,findReviews.hasNext(),next_cursor);
    }
}
