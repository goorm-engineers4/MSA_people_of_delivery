package com.example.cloudfour.authservice.domain.auth.entity;

import com.example.cloudfour.authservice.domain.auth.enums.VerificationPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class VerificationCode {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public boolean isExpired() { return LocalDateTime.now().isAfter(expiryDate); }
}