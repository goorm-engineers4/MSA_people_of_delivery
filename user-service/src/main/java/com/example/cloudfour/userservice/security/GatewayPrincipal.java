package com.example.cloudfour.userservice.security;


import java.util.UUID;

public record GatewayPrincipal(UUID userId, String role) {}

