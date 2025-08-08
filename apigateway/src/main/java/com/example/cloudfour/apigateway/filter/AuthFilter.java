package com.example.cloudfour.apigateway.filter;

import com.example.cloudfour.apigateway.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final JwtUtil jwtUtil;

    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest req = exchange.getRequest();

            String bearer = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String access = (StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))
                    ? bearer.substring(7)
                    : req.getHeaders().getFirst("Access");

            if (!StringUtils.hasText(access) || !jwtUtil.tokenValidation(access)) {
                ServerHttpRequest mutated = req.mutate()
                        .header("Auth", "false")
                        .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            }

            String user = jwtUtil.getIdFromToken(access);
            String role = jwtUtil.getRoleFromToken(access);

            ServerHttpRequest mutated = req.mutate() // 대칭키 암호화 방식도 생각해보기 ..
                    .header("Auth", "true")
                    .header("Account-Value", user != null ? user : "")
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        };
    }
}
