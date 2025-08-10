package com.example.cloudfour.aiservice.repository;

import com.example.cloudfour.aiservice.entity.AiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiLogRepository extends JpaRepository<AiLog, UUID> {
    
    List<AiLog> findByRequestTypeOrderByCreatedAtDesc(String requestType);

    List<AiLog> findBySuccessOrderByCreatedAtDesc(boolean success);
}
