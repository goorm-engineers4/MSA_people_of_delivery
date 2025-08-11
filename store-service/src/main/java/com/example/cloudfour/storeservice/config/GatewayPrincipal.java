package com.example.cloudfour.storeservice.config;

import java.util.UUID;

public record GatewayPrincipal(UUID userId, String role) {}