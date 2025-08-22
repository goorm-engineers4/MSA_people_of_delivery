package com.example.cloudfour.paymentservice.domain.payment.repository;

import com.example.cloudfour.paymentservice.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByPaymentKey(String paymentKey);
    List<Payment> findAllByOrder_Store_Id(UUID storeId);
    List<Payment> findAllByOrder_User_Id(UUID userId);
    boolean existsByPaymentKey(String paymentKey);
}
