package com.ege.wooda.domain.member.dto.response;

import com.ege.wooda.global.security.jwt.dto.JwtToken;

public record AuthTokenResponse(String accessToken, String refreshToken) {

    public static AuthTokenResponse of(JwtToken jwtToken) {
        return new AuthTokenResponse(jwtToken.accessToken(), jwtToken.refreshToken());
    }
}
