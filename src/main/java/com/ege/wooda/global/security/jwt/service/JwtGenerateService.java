package com.ege.wooda.global.security.jwt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ege.wooda.global.security.jwt.dto.JwtToken;
import com.ege.wooda.global.security.jwt.util.JwtProvider;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtGenerateService {
    private final JwtProvider jwtProvider;

    @Value("${jwt.access-header}")
    private String accessTokenHeaderTag;

    @Value("${jwt.refresh-header}")
    private String refreshTokenHeaderTag;

    public JwtToken createJwtTokenByAuthToken(String authToken) {
        return jwtProvider.createJwtTokenByAuthToken(authToken);
    }

    public String createAuthToken(PrincipalUser principalUser) {
        return jwtProvider.createAuthToken(principalUser);
    }
}
