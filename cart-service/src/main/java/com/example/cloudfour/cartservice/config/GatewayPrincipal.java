package com.example.cloudfour.cartservice.config;

import java.util.UUID;

public record GatewayPrincipal(UUID userId, String role) {}