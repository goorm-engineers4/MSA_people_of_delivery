package com.example.cloudfour.apigateway.controller;

import com.example.cloudfour.apigateway.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-service/auth")
public class AuthProxyController {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        Map userInfo = restTemplate.postForObject("http://user-service/auth/login", body, Map.class);
        Object result = userInfo.get("result");
        Map<String, Object> payload = result instanceof Map ? (Map<String, Object>) result : userInfo;
        String userId = String.valueOf(payload.get("userId"));
        String role = String.valueOf(payload.get("role"));

        String access = jwtUtil.createAccessToken(userId, role);
        String refresh = jwtUtil.createRefreshToken(userId, role);

        return ResponseEntity.ok(Map.of(
                "accessToken", access,
                "refreshToken", refresh,
                "expiresIn", jwtUtil.getAccessTokenTtlSeconds(),
                "issuedAt", Instant.now().toString()
        ));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, Object> body) {
        String refresh = String.valueOf(body.get("refreshToken"));
        if (!jwtUtil.tokenValidation(refresh)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid refresh token"));
        }
        String userId = jwtUtil.getIdFromToken(refresh);
        String role = jwtUtil.getRoleFromToken(refresh);
        String newAccess = jwtUtil.createAccessToken(userId, role);
        return ResponseEntity.ok(Map.of(
                "accessToken", newAccess,
                "expiresIn", jwtUtil.getAccessTokenTtlSeconds()
        ));
    }
}


