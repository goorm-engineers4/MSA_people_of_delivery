package com.example.cloudfour.authservice.domain.auth.service;

import com.example.cloudfour.authservice.client.UserClient;
import com.example.cloudfour.authservice.domain.auth.dto.AuthModelDTO;
import com.example.cloudfour.authservice.domain.auth.dto.UserRequestDTO;
import com.example.cloudfour.authservice.domain.auth.converter.AuthConverter;
import com.example.cloudfour.authservice.domain.auth.dto.AuthRequestDTO;
import com.example.cloudfour.authservice.domain.auth.dto.AuthResponseDTO;
import com.example.cloudfour.authservice.domain.auth.dto.TokenDTO;
import com.example.cloudfour.authservice.domain.auth.enums.VerificationPurpose;
import com.example.cloudfour.authservice.domain.auth.exception.AuthErrorCode;
import com.example.cloudfour.authservice.domain.auth.exception.AuthException;
import com.example.cloudfour.authservice.domain.auth.repository.VerificationCodeRepository;
import com.example.cloudfour.authservice.domain.auth.service.JwtService;
import com.example.cloudfour.authservice.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserClient userClient;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final RedisUtil redisUtil;
    private final JwtService jwtService;

    private static final int CODE_LEN = 6;
    private static final int CODE_EXP_MIN = 10;

    public AuthResponseDTO.AuthRegisterResponseDTO register(AuthRequestDTO.RegisterRequestDTO request){
        String email = request.email().toLowerCase();

        if (userClient.existsByEmailBool(email)) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_USED);
        }

        var created = userClient.create(new UserRequestDTO.CreateUserRequestDTO(
                email,
                request.nickname(),
                request.number(),
                request.password(),
                request.role()
        ));

        var model = new AuthModelDTO.RegisterResultDTO(
                created.id(),
                created.email(),
                request.nickname(),
                created.role()
        );

        return AuthConverter.toAuthRegisterResponseDTO(model);
    }

    public AuthResponseDTO.AuthTokenResponseDTO login(AuthRequestDTO.LoginRequestDTO request) {
        String email = request.email().toLowerCase();

        var user = userClient.byEmail(email);

        if (!user.emailVerified()) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        var pw = userClient.verifyPassword(user.id(), request.password());
        if (pw == null || !pw.match()) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID);
        }

        log.info("로그인 성공: userId={}", user.id());

        String access  = jwtService.createAccess(user.id(), user.role());
        String refresh = jwtService.createRefresh(user.id(), user.role());

        var token = new TokenDTO("Bearer", access, refresh, jwtService.accessTtlSeconds());
        redisUtil.save(user.email(), refresh);

        return AuthConverter.toAuthTokenResponseDTO(token);
    }

    public void logout(String accessHeader) {
        String token = stripBearer(accessHeader);
        String userId = jwtService.userId(token);
        var user = userClient.byId(UUID.fromString(userId));

        redisUtil.delete(user.email());
        log.info("로그아웃: userId={}", user.id());
    }

    public AuthResponseDTO.AuthTokenResponseDTO refreshAccessToken(AuthRequestDTO.RefreshTokenRequestDTO request) {
        String refresh = request.refreshToken();

        if (refresh == null || !jwtService.isValid(refresh)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        var jwt = jwtService.decode(refresh);
        String typ = jwt.getClaimAsString("typ");
        if (!"refresh".equals(typ)) {
            throw new AuthException(AuthErrorCode.TOKEN_TYPE_INVALID);
        }

        String userId = jwt.getSubject();
        var user = userClient.byId(UUID.fromString(userId));

        String savedRefreshToken = redisUtil.get(user.email());
        if (savedRefreshToken == null || !savedRefreshToken.equals(refresh)) {
            throw new AuthException(AuthErrorCode.REFRESH_NOT_MATCHED);
        }

        String access  = jwtService.createAccess(user.id(), user.role());
        String newRefresh = jwtService.createRefresh(user.id(), user.role());
        redisUtil.save(user.email(), newRefresh);

        var token = new TokenDTO("Bearer", access, newRefresh, jwtService.accessTtlSeconds());
        return AuthConverter.toAuthTokenResponseDTO(token);
    }

    public void changePassword(UUID userId, AuthRequestDTO.PasswordChangeDto request) {
        userClient.changePassword(userId, request.currentPassword(), request.newPassword());

        var user = userClient.byId(userId);
        redisUtil.delete(user.email());
    }

    public void sendVerificationEmail(String email) {
        Objects.requireNonNull(email, "email은 null일 수 없습니다.");
        String target = email.toLowerCase();

        String code = generateCode(CODE_LEN);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(CODE_EXP_MIN);

        verificationCodeRepository.deleteByEmail(target);
        verificationCodeRepository.save(
                AuthConverter.toVerificationCode(target, code, expiry, VerificationPurpose.EMAIL_VERIFY)
        );
        log.info("이메일 인증 코드 저장: email={}, expiry={}", target, expiry);

        String title = "이메일 인증 번호";
        String content = """
                <html><body>
                <h1>인증 코드 : %s</h1>
                <p>해당 코드를 홈페이지에 입력하세요.</p>
                <p>* 본 메일은 자동응답 메일입니다.</p>
                </body></html>
                """.formatted(code);
        try{
            emailService.sendSimpleMessage(target, title, content);
        }catch(RuntimeException | MessagingException e){
            throw new AuthException(AuthErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void verifyEmailCode(AuthRequestDTO.EmailVerifyRequestDTO request) {
        String target = request.email().toLowerCase();
        Objects.requireNonNull(request.code(), "code는 null일 수 없습니다.");

        var vc = verificationCodeRepository
                .findByEmailAndCodeAndPurpose(target, request.code(), VerificationPurpose.EMAIL_VERIFY)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_CODE_INVALID));

        if (vc.isExpired()) {
            verificationCodeRepository.delete(vc);
            throw new AuthException(AuthErrorCode.EMAIL_CODE_EXPIRED);
        }

        var user = userClient.byEmail(target);
        userClient.markEmailVerified(user.id());
        verificationCodeRepository.delete(vc);

        log.info("이메일 인증 완료: email={}", target);
    }

    public void startEmailChange(UUID userId, String newEmail) {
        String target = newEmail.toLowerCase();

        var u = userClient.byId(userId);
        if (u.email().equalsIgnoreCase(target)) {
            throw new AuthException(AuthErrorCode.EMAIL_SAME_AS_OLD);
        }
        if (userClient.existsByEmailBool(target)) {
            throw new AuthException(AuthErrorCode.EMAIL_IN_USE);
        }

        userClient.startEmailChange(userId, target);

        verificationCodeRepository.deleteByEmailAndPurpose(target, VerificationPurpose.CHANGE_EMAIL);

        var code = generateCode(CODE_LEN);
        verificationCodeRepository.save(
                AuthConverter.toVerificationCode(
                        target,
                        code,
                        LocalDateTime.now().plusMinutes(CODE_EXP_MIN),
                        VerificationPurpose.CHANGE_EMAIL
                )
        );

        String title = "이메일 변경 인증 번호";
        String content = """
                <html><body>
                <h1>인증 코드 : %s</h1>
                <p>해당 코드를 홈페이지에 입력하세요.</p>
                <p>* 본 메일은 자동응답 메일입니다.</p>
                </body></html>
                """.formatted(code);

        try{
            emailService.sendSimpleMessage(target, title, content);
        }catch(RuntimeException | MessagingException e){
            throw new AuthException(AuthErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void verifyEmailChange(UUID userId, String newEmail, String code) {
        String target = newEmail.toLowerCase();

        if (userClient.existsByEmailBool(target)) {
            throw new AuthException(AuthErrorCode.EMAIL_IN_USE);
        }

        var vc = verificationCodeRepository
                .findByEmailAndCodeAndPurpose(target, code, VerificationPurpose.CHANGE_EMAIL)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_CODE_INVALID));

        if (vc.isExpired()) {
            verificationCodeRepository.delete(vc);
            throw new AuthException(AuthErrorCode.EMAIL_CODE_EXPIRED);
        }

        userClient.confirmEmailChange(userId, target);

        verificationCodeRepository.delete(vc);
        log.info("이메일 변경 확정: userId={}, newEmail={}", userId, target);
    }

    private String generateCode(int len) {
        var r = new Random();
        var sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    private String stripBearer(String value) {
        return value != null && value.startsWith("Bearer ") ? value.substring(7) : value;
    }
}