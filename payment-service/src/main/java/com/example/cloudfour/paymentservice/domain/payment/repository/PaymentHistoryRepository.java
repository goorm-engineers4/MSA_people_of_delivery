package com.example.cloudfour.paymentservice.domain.payment.repository;

import com.example.cloudfour.paymentservice.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, UUID> {
}
