package com.ege.wooda.global.security.oauth.ios.service;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.stereotype.Service;

import com.ege.wooda.domain.member.service.MemberService;
import com.ege.wooda.global.security.jwt.util.JwtValidator;
import com.ege.wooda.global.security.oauth.common.mapper.PrincipalUserMapper;
import com.ege.wooda.global.security.oauth.ios.dto.AppleAuthRequest;
import com.ege.wooda.global.security.oauth.ios.feign.AppleAuthClient;
import com.ege.wooda.global.security.oauth.ios.util.ApplePublicKeyGenerator;
import com.ege.wooda.global.security.oauth.model.OAuth2Request;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppleAuthService {
    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtValidator jwtValidator;
    private final MemberService memberService;
    private final PrincipalUserMapper principalUserMapper;

    public PrincipalUser loadUser(AppleAuthRequest appleAuthRequest)
            throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException,
                   JsonProcessingException {
        String accountId = getAppleAccountId(appleAuthRequest.identityToken());
        String name = appleAuthRequest.user().name().lastName() + appleAuthRequest.user().name().firstName();
        String email = appleAuthRequest.user().email();

        OAuth2Request oAuth2Request = OAuth2Request.builder()
                                                   .accountId(accountId)
                                                   .name(name)
                                                   .email(email)
                                                   .build();

        return principalUserMapper.toPrincipalUser(memberService.saveIfNewMember(oAuth2Request));
    }

    public String getAppleAccountId(String identityToken)
            throws JsonProcessingException, AuthenticationException, NoSuchAlgorithmException,
                   InvalidKeySpecException {
        Map<String, String> headers = jwtValidator.parseHeaders(identityToken);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers,
                                                                        appleAuthClient.getAppleAuthPublicKey());

        return jwtValidator.getTokenClaims(identityToken, publicKey).getSubject();
    }
}
