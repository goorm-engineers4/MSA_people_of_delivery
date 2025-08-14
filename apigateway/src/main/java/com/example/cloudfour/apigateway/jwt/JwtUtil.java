package com.example.cloudfour.apigateway.jwt;

import ch.qos.logback.classic.Logger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties props;

    public boolean tokenValidation(String token) {
        try {
            parseClaims(token);
            return true; }
        catch (Exception e) {
            return false;
        }
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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Mono<Void> onError(ServerWebExchange exchange, String msg, HttpStatus status) {
        var res = exchange.getResponse();
        res.setStatusCode(status);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        var body = ("{\"success\":false,\"code\":\"" + status.value() + "\",\"message\":\"" + msg + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return res.writeWith(Mono.just(res.bufferFactory().wrap(body)));
    }

    public String createAccessToken(String userId, String role) {
        Date now = new Date();
        Key key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setIssuer(props.getIssuer())
                .setAudience(props.getAudience())
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + props.getExpiration()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String userId, String role) {
        Date now = new Date();
        Key key = Keys.hmacShaKeyFor(props.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setIssuer(props.getIssuer())
                .setAudience(props.getAudience())
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + props.getRefreshExpiration()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenTtlSeconds() {
        return props.getExpiration();
    }
}