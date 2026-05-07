package com.KahoanDev.products_api.Security.dto;

public record LoginResponse(
        String token,
        String type,
        long expiresIn
) {
    public static LoginResponse of(String token, long expirationMs) {
        return new LoginResponse(token, "Bearer", expirationMs / 1000);
    }
}
