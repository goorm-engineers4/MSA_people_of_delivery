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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
public class ReviewQueryService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final RestTemplate restTemplate;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);
    private final HttpServletRequest request;

    public ReviewResponseDTO.ReviewDetailResponseDTO getReviewById(UUID reviewId, GatewayPrincipal user) {
        if(user==null){
            log.warn("상세 리뷰 조회 접근 권한 없음");
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("상세 리뷰 조회 권한 확인 성공");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Auth", request.getHeader("Auth"));
        headers.add("Account-Value", request.getHeader("Account-Value"));
        headers.add("X-User-Role", request.getHeader("X-User-Role"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<UserResponseDTO> response = restTemplate.exchange("http://localhost:8080/api/user-service/users/me", HttpMethod.GET,entity, UserResponseDTO.class);
        UserResponseDTO findUser = response.getBody();
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(()->{
            log.warn("존재하지 않는 리뷰");
            return new ReviewException(ReviewErrorCode.NOT_FOUND);
        });
        log.info("상세 리뷰 조회 성공");
        return ReviewConverter.toReviewDetailResponseDTO(findReview, findUser.getNickname());
    }

    public ReviewResponseDTO.ReviewStoreListResponseDTO getReviewListByStore(UUID storeId, LocalDateTime cursor, Integer size, GatewayPrincipal user) {
        storeRepository.findById(storeId).orElseThrow(()->{
            log.warn("존재하지 않는 가게");
            return new StoreException(StoreErrorCode.NOT_FOUND);
        });
        if(user==null){
            log.warn("가게 리뷰 목록 조회 접근 권한 없음");
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_ACCESS);
        }
        if(cursor==null){
            cursor = first_cursor;
        }
        log.info("가게 리뷰 목록 조회 권한 확인 성공");
        Pageable pageable = PageRequest.of(0,size);

        Slice<Review> findReviews = reviewRepository.findAllByStoreId(storeId,cursor,pageable);
        if(findReviews.isEmpty()){
            log.info("가게 리뷰 데이터 없음");
            throw new ReviewException(ReviewErrorCode.NOT_FOUND);
        }
        List<Review> reviews = findReviews.toList();
        List<ReviewResponseDTO.ReviewStoreResponseDTO> reviewStoreListResponseDTOS = reviews.stream().map(ReviewConverter::toReviewStoreResponseDTO).toList();
        LocalDateTime next_cursor = null;
        if(!findReviews.isEmpty() && findReviews.hasNext()) {
            next_cursor = reviews.getLast().getCreatedAt();
        }
        log.info("가게 리뷰 목록 조회 성공");
        return ReviewConverter.toReviewStoreListResponseDTO(reviewStoreListResponseDTOS,findReviews.hasNext(),next_cursor);
    }

    public ReviewResponseDTO.ReviewUserListResponseDTO getReviewListByUser(LocalDateTime cursor, Integer size, GatewayPrincipal user) {
        if(user==null){
            log.warn("가게 리뷰 목록 조회 접근 권한 없음");
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_ACCESS);
        }
        if(cursor==null){
            cursor = first_cursor;
        }
        log.info("사용자 리뷰 목록 조회 권한 확인 성공");
        Pageable pageable = PageRequest.of(0,size);
        Slice<Review> findReviews = reviewRepository.findAllByUserId(user.userId(),cursor,pageable);
        if(findReviews.isEmpty()){
            log.info("사용자 리뷰 데이터 없음");
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
