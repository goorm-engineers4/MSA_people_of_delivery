package com.example.cloudfour.userservice.security.jwt.util;

import com.example.cloudfour.userservice.domain.user.enums.Role;
import com.example.cloudfour.userservice.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties props;

    public boolean tokenValidation(String token) {
        try { parseClaims(token); return true; }
        catch (Exception e) { return false; }
    }

    public String getIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        Object r = parseClaims(token).get("role");
        return r == null ? null : r.toString();
    }

    private Claims parseClaims(String token) {
        Key key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String createAccessToken(UUID userId, Role role) {
        long now = System.currentTimeMillis();
        long exp = now + (props.getExpiration() * 1000L);
        Key key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new java.util.Date(now))
                .setExpiration(new java.util.Date(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(UUID userId, Role role) {
        long now = System.currentTimeMillis();
        long exp = now + (props.getExpiration() * 1000L * 24 * 7);
        Key key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new java.util.Date(now))
                .setExpiration(new java.util.Date(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenTtlSeconds() {
        return props.getExpiration();
    }
}