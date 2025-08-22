package com.example.cloudfour.storeservice.scheduler;

import com.example.cloudfour.storeservice.domain.collection.repository.command.ReviewCommandRepository;
import com.example.cloudfour.storeservice.domain.review.entity.Review;
import com.example.cloudfour.storeservice.domain.review.repository.ReviewRepository;
import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MongoUpdatedSyncScheduler {
    private final ReviewCommandRepository reviewCommandRepository;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    @Scheduled(cron = "0 * * * * *")
    public void refreshReviews(){
        log.info("MongoDB에 리뷰 최신화 시작");
        Pageable pageable = PageRequest.of(0,3);
        List<Store> stores = storeRepository.findAllByIsDeletedIsFalse();
        for(Store store:stores){
            Slice<Review> top3Review = reviewRepository.findAllTopThreeReview(store.getId(),pageable);
            List<Review> reviews = top3Review.toList();
            reviewCommandRepository.createReviewByStoreId(store.getId(), reviews);
            reviewCommandRepository.updateStoreReview(store.getId(),store.getReviewCount(),store.getRating());
            log.info("MongoDB에 리뷰 최신화 완료");
        }
    }
}
