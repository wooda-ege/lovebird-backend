package com.ege.wooda.global.security.jwt.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ege.wooda.domain.member.domain.Member;
import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.security.oauth.common.mapper.PrincipalUserMapper;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final Key key;
    private final MemberService memberService;
    private final PrincipalUserMapper principalUserMapper;

    public Authentication getAuthentication(String accessToken) {
        Claims claims = getTokenClaims(accessToken);
        Member member = memberService.findById(Long.parseLong(claims.get("id", String.class)));
        PrincipalUser principalUser = principalUserMapper.toPrincipalUser(member);

        return new UsernamePasswordAuthenticationToken(principalUser, "", principalUser.getAuthorities());
    }

    public PrincipalUser getPrincipalUser(String token) {
        Claims claims = getTokenClaims(token);
        Member member = memberService.findById(Long.parseLong(claims.get("id", String.class)));
        return principalUserMapper.toPrincipalUser(member);
    }

    public Map<String, String> parseHeaders(String token) throws JsonProcessingException {
        String header = token.split("\\.")[0];
        return new ObjectMapper().readValue(decodeHeader(header), Map.class);
    }

    public String decodeHeader(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }

    private Claims getTokenClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public Claims getTokenClaims(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                   .setSigningKey(publicKey)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
}
