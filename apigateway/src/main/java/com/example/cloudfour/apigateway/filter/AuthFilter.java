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
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();

            String bearer = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String access = (StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))
                    ? bearer.substring(7)
                    : request.getHeaders().getFirst("Access");

            if (!StringUtils.hasText(access) || !jwtUtil.tokenValidation(access)) {
                ServerHttpRequest mutated = request.mutate()
                        .header("Auth", "false")
                        .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            }

            String user = jwtUtil.getIdFromToken(access);
            String role = jwtUtil.getRoleFromToken(access);

            ServerHttpRequest mutated = request.mutate() // 대칭키 암호화 방식도 생각해보기 ..
                    .header("Auth", "true")
                    .header("Account-Value", user != null ? user : "")
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            return chain.filter(exchange);
        };
    }
}
