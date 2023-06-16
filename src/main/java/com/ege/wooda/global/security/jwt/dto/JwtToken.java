package com.ege.wooda.global.security.jwt.dto;

public record JwtToken(String accessToken, String refreshToken, String grantType) {

    public JwtToken {}
}
