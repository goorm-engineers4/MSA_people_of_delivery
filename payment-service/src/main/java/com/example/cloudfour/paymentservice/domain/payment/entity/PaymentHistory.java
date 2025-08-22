package com.example.cloudfour.paymentservice.domain.payment.entity;

import com.example.cloudfour.paymentservice.domain.payment.enums.PaymentStatus;
import com.example.cloudfour.modulecommon.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_payment_history")
public class PaymentHistory extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Lob
    private String failedReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;

    public static class PaymentHistoryBuilder{
        private PaymentHistoryBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수동 생성 불가");
        }
    }

    public void setPayment(Payment payment){
        this.payment = payment;
        payment.setPaymentHistory(this);
    }
}
