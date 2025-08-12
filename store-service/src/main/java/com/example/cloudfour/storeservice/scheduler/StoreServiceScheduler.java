package com.example.cloudfour.storeservice.scheduler;

import com.example.cloudfour.storeservice.domain.review.repository.ReviewRepository;
import com.example.cloudfour.storeservice.domain.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StoreServiceScheduler {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteReview(){
        LocalDateTime threeDays = LocalDateTime.now().minusDays(3);
        reviewRepository.deleteAllByCreatedAtBefore(threeDays);
        log.info("Soft Deleted된 Review 삭제 (7일)");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteStore(){
        LocalDateTime sevenDays = LocalDateTime.now().minusDays(7);
        storeRepository.deleteAllByCreatedAtBefore(sevenDays);
        log.info("Soft Deleted된 Store 삭제 (7일)");
    }
}
