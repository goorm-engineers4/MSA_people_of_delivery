package com.example.cloudfour.userservice.domain.auth.repository;

import com.example.cloudfour.userservice.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
