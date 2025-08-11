package com.example.cloudfour.storeservice.config.security;

import com.example.cloudfour.storeservice.config.GatewayPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class GatewayAuthHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Auth");
        String user = request.getHeader("Account-Value");
        String role = request.getHeader("X-User-Role");

        if ("true".equalsIgnoreCase(auth) && user != null && !user.isBlank()) {
            Collection<? extends GrantedAuthority> authorities =
                    (role == null)
                            ? java.util.Collections.<GrantedAuthority>emptyList()
                            : java.util.List.of(new SimpleGrantedAuthority(role));

            UUID userId;
            try {
                userId = UUID.fromString(user);
            } catch (IllegalArgumentException e) {
                chain.doFilter(request, response);
                return;
            }

            var principal = new GatewayPrincipal(userId, role);
            var authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}