package com.example.cloudfour.userservice.domain.user.service;


import com.example.cloudfour.userservice.domain.user.converter.UserConverter;
import com.example.cloudfour.userservice.domain.user.dto.UserResponseDTO;
import com.example.cloudfour.userservice.domain.user.entity.User;
import com.example.cloudfour.userservice.domain.user.repository.UserRepository;
import com.example.cloudfour.userservice.domain.user.exception.UserErrorCode;
import com.example.cloudfour.userservice.domain.user.exception.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO.MeResponseDTO getMyInfo(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        var dto = UserConverter.toMeResponseDTO(u);
        log.info("내 정보 조회: userId={}", userId);
        return dto;
    }

    @Transactional
    public void updateProfile(UUID userId, String nickname, String number) {
        User u = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        if (nickname != null && !nickname.isBlank() && !nickname.equals(u.getNickname())) {
            u.changeNickname(nickname);
            log.info("닉네임 변경: userId={}, nickname={}", userId, nickname);
        }
        if (number != null && !number.isBlank() && !number.equals(u.getNumber())) {
            u.changeNumber(number);
            log.info("전화번호 변경: userId={}, number={}", userId, number);
        }
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User u = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));
        u.softDelete();
        log.info("회원 탈퇴 처리: userId={}", userId);
    }
}


