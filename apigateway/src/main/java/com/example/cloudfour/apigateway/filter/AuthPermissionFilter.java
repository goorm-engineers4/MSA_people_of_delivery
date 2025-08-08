package com.example.cloudfour.apigateway.filter;

import com.example.cloudfour.apigateway.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class AuthPermissionFilter extends AbstractGatewayFilterFactory<AuthPermissionFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthPermissionFilter(JwtUtil jwtUtil) {
        super(AuthPermissionFilter.Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var req = exchange.getRequest();

            String bearer = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String access = (StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))
                    ? bearer.substring(7)
                    : req.getHeaders().getFirst("Access");

            if (!StringUtils.hasText(access)) {
                return jwtUtil.onError(exchange, "No Access token", HttpStatus.UNAUTHORIZED);
            }
            if (!jwtUtil.tokenValidation(access)) {
                return jwtUtil.onError(exchange, "AccessToken is not valid", HttpStatus.UNAUTHORIZED);
            }

            var mutated = req.mutate()
                    .header("Auth", "true")
                    .header("Account-Value", jwtUtil.getIdFromToken(access))
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        };
    }
}