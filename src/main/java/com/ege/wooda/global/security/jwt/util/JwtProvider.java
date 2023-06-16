package com.ege.wooda.global.security.jwt.util;

import java.security.Key;

import org.springframework.stereotype.Component;

import com.ege.wooda.global.security.jwt.dto.JwtToken;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final Long AUTH_TOKEN_VALIDATION_SECOND = 60L * 1000; // 1minute
    private static final Long ACCESS_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 1000; // 24hours
    private static final Long REFRESH_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 14 * 1000; // 14days
    private static final String BEARER_TYPE = "bearer";

    private final Key key;
    private final JwtValidator jwtValidator;

    public JwtToken createJwtToken(PrincipalUser principalUser) {
        Claims claims = getClaims(principalUser);

        String accessToken = getToken(principalUser, claims, ACCESS_TOKEN_VALIDATION_SECOND);
        String refreshToken = getToken(principalUser, claims, REFRESH_TOKEN_VALIDATION_SECOND);

        return new JwtToken(accessToken, refreshToken, BEARER_TYPE);
    }

    public JwtToken createJwtTokenByAuthToken(String authToken) {
        return createJwtToken(jwtValidator.getPrincipalUser(authToken));
    }

    public String createAuthToken(PrincipalUser principalUser) {
        return getToken(principalUser, getClaims(principalUser), AUTH_TOKEN_VALIDATION_SECOND);
    }

    public Claims getClaims(PrincipalUser principalUser) {
        Claims claims = Jwts.claims();
        claims.put("id", principalUser.getName());
        return claims;
    }

    private String getToken(PrincipalUser principalUser, Claims claims, Long validationSecond) {
        long now = new Date().getTime();

        return Jwts.builder()
                   .setSubject(principalUser.getName())
                   .setClaims(claims)
                   .signWith(key, SignatureAlgorithm.HS512)
                   .setExpiration(new Date(now + validationSecond))
                   .compact();
    }
}
