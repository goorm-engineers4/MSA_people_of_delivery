package com.example.cloudfour.userservice.domain.auth.service;

import com.example.cloudfour.userservice.domain.auth.controller.AuthCommonResponseDTO;
import com.example.cloudfour.userservice.domain.auth.converter.AuthConverter;
import com.example.cloudfour.userservice.domain.auth.dto.AuthResponseDTO;
import com.example.cloudfour.userservice.domain.auth.dto.TokenDTO;
import com.example.cloudfour.userservice.domain.auth.exception.AuthErrorCode;
import com.example.cloudfour.userservice.domain.auth.exception.AuthException;
import com.example.cloudfour.userservice.domain.user.entity.User;
import com.example.cloudfour.userservice.domain.user.enums.LoginType;
import com.example.cloudfour.userservice.domain.user.enums.Role;
import com.example.cloudfour.userservice.domain.auth.enums.VerificationPurpose;
import com.example.cloudfour.userservice.domain.auth.dto.AuthRequestDTO;
import com.example.cloudfour.userservice.domain.user.repository.UserRepository;
import com.example.cloudfour.userservice.domain.auth.entity.VerificationCode;
import com.example.cloudfour.userservice.domain.auth.repository.VerificationCodeRepository;
import com.example.cloudfour.userservice.properties.JwtProperties;
import com.example.cloudfour.userservice.security.jwt.JwtUtil;
import com.example.cloudfour.userservice.security.jwt.repository.TokenRepository;
import com.example.cloudfour.userservice.security.jwt.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;
    private final RedisUtil redisUtil;

    private static final int CODE_LEN = 6;
    private static final int CODE_EXP_MIN = 10;

    public AuthResponseDTO.AuthRegisterResponseDTO registercustomer(AuthRequestDTO.RegisterRequestDTO request){
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_USED);
        }

        var common = AuthCommonResponseDTO.builder()
                .email(email)
                .nickname(request.nickname())
                .encodedPassword(passwordEncoder.encode(request.password()))
                .number(request.number())
                .role(Role.ROLE_CUSTOMER)
                .build();
        User user = AuthConverter.toUser(common);

        userRepository.save(user);
        log.info("고객 회원가입 완료: userId={}", user.getId());

        return AuthConverter.toAuthRegisterResponseDTO(user);
    }

    public AuthResponseDTO.AuthRegisterResponseDTO registerowner(AuthRequestDTO.RegisterRequestDTO request){
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_USED);
        }

        var common = AuthCommonResponseDTO.builder()
                .email(email)
                .nickname(request.nickname())
                .encodedPassword(passwordEncoder.encode(request.password()))
                .number(request.number())
                .role(Role.ROLE_OWNER)
                .build();
        User user = AuthConverter.toUser(common);

        userRepository.save(user);
        log.info("점주 회원가입 완료: userId={}", user.getId());

        return AuthConverter.toAuthRegisterResponseDTO(user);
    }

    public AuthResponseDTO.AuthTokenResponseDTO login(AuthRequestDTO.LoginRequestDTO request) {
        String email = request.email().toLowerCase();

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID);
        }

        if (user.getLoginType() != LoginType.LOCAL) {
            throw new AuthException(AuthErrorCode.SOCIAL_LOGIN_NOT_ALLOWED);
        }
        if (user.getIsDeleted()) {
            throw new AuthException(AuthErrorCode.ACCOUNT_DELETED);
        }
        if (!user.isEmailVerified()) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        log.info("로그인 성공: userId={}", user.getId());

        String access  = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());

        TokenDTO token = new TokenDTO("Bearer", access, refresh, jwtProperties.getExpiration());

        redisUtil.save(user.getEmail(), refresh);

        return AuthConverter.toAuthTokenResponseDTO(token);
    }

    public void logout(String accessToken) {
        String token = accessToken.substring(7);

        UUID userId = UUID.fromString(jwtUtil.getIdFromToken(token));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        tokenRepository.deleteById(user.getEmail());
    }

    public AuthResponseDTO.AuthTokenResponseDTO refreshAccessToken(AuthRequestDTO.RefreshTokenRequestDTO request) {
        String refresh =  request.refreshToken();

        if ("null".equals(refresh) || !jwtUtil.tokenValidation(refresh)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        String userId = jwtUtil.getIdFromToken(refresh);

        User user = userRepository.findByIdAndIsDeletedFalse(UUID.fromString(userId))
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        String savedRefreshToken = redisUtil.get(user.getEmail());
        if (savedRefreshToken == null) {
            throw new IllegalArgumentException("저장된 Refresh Token이 없습니다.");
        }

        if (!savedRefreshToken.equals(refresh)) {
            throw new IllegalArgumentException("Refresh Token이 불일치 합니다.");
        }

        String access  = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String newRefresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());

        TokenDTO token = new TokenDTO("Bearer", access, newRefresh, jwtProperties.getExpiration());

        redisUtil.save(user.getEmail(), newRefresh);

        return AuthConverter.toAuthTokenResponseDTO(token);
    }

    public void changePassword(UUID userId, AuthRequestDTO.PasswordChangeDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (user.getLoginType() != LoginType.LOCAL) {
            throw new IllegalArgumentException("소셜 로그인 계정은 비밀번호 변경 대상이 아닙니다.");
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        user.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    public void sendVerificationEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");

        String code = generateCode(CODE_LEN);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(CODE_EXP_MIN);

        verificationCodeRepository.deleteByEmail(email);
        verificationCodeRepository.save(
                AuthConverter.toVerificationCode(email, code, expiry, VerificationPurpose.EMAIL_VERIFY)
        );
        log.info("이메일 인증 코드 저장: email={}, expiry={}", email, expiry);

        String title = "이메일 인증 번호";
        String content = "<html>"
                + "<body>"
                + "<h1> 인증 코드 : " + code + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<p> * 본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                +"</footer>"
                + "</body>"
                + "</html>";
        try{
            emailService.sendSimpleMessage(email, title, content);
        }catch(RuntimeException | MessagingException e){
            e.printStackTrace();
            throw new AuthException(AuthErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void verifyEmailCode(AuthRequestDTO.EmailVerifyRequestDTO request) {
        Objects.requireNonNull(request, "request null");
        Objects.requireNonNull(request.email(), "email null");
        Objects.requireNonNull(request.code(), "code null");

        VerificationCode vc = verificationCodeRepository
                .findByEmailAndCode(request.email(), request.code())
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_CODE_INVALID));
        if (vc.isExpired()) {
            verificationCodeRepository.delete(vc);
            throw new AuthException(AuthErrorCode.EMAIL_CODE_EXPIRED);
        }
        verificationCodeRepository.delete(vc);

        userRepository.findByEmailAndIsDeletedFalse(request.email())
                .ifPresent(User::markEmailVerified);
        log.info("이메일 인증 완료: email={}", request.email());
    }

    public void startEmailChange(UUID userId, String newEmail) {
        User u = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (u.getEmail().equalsIgnoreCase(newEmail)) {
            throw new AuthException(AuthErrorCode.EMAIL_SAME_AS_OLD);
        }
        if (userRepository.existsByEmailAndIsDeletedFalse(newEmail)) {
            throw new AuthException(AuthErrorCode.EMAIL_IN_USE);
        }

        u.requestEmailChange(newEmail);

        verificationCodeRepository.deleteByEmailAndPurpose(newEmail, VerificationPurpose.CHANGE_EMAIL);

        var code = generateCode(CODE_LEN);
        verificationCodeRepository.save(
                AuthConverter.toVerificationCode(
                        newEmail, code, LocalDateTime.now().plusMinutes(CODE_EXP_MIN), VerificationPurpose.CHANGE_EMAIL)
        );
        log.info("이메일 변경 코드 저장: userId={}, newEmail={}", userId, newEmail);

        String title = "이메일 인증 번호";
        String content = "<html>"
                + "<body>"
                + "<h1> 인증 코드 : " + code + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<p> * 본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                +"</footer>"
                + "</body>"
                + "</html>";
        try{
            emailService.sendSimpleMessage(newEmail, title, content);
        }catch(RuntimeException | MessagingException e){
            e.printStackTrace();
            throw new AuthException(AuthErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void verifyEmailChange(UUID userId, String newEmail, String code) {
        User u = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (u.getPendingEmail() == null || !u.getPendingEmail().equalsIgnoreCase(newEmail)) {
            throw new IllegalArgumentException("변경 대기 중인 이메일이 일치하지 않습니다.");
        }

        var vc = verificationCodeRepository
                .findByEmailAndCodeAndPurpose(newEmail, code, VerificationPurpose.CHANGE_EMAIL)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_CODE_INVALID));

        if (vc.isExpired()) {
            verificationCodeRepository.delete(vc);
            throw new AuthException(AuthErrorCode.EMAIL_CODE_EXPIRED);
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(newEmail)) {
            throw new AuthException(AuthErrorCode.EMAIL_IN_USE);
        }

        u.confirmEmailChange();
        verificationCodeRepository.delete(vc);
        log.info("이메일 변경 확정: userId={}, newEmail={}", userId, newEmail);

    }

    private String generateCode(int len) {
        var r = new Random();
        var sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }
}
