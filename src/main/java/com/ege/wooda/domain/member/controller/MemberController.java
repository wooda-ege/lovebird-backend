package com.ege.wooda.domain.member.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.naming.AuthenticationException;

import com.ege.wooda.domain.member.dto.request.AuthRequest;
import com.ege.wooda.domain.member.dto.response.AuthTokenResponse;
import com.ege.wooda.domain.member.dto.response.MemberResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;
import com.ege.wooda.global.security.jwt.service.JwtGenerateService;
import com.ege.wooda.global.security.oauth.ios.dto.AppleAuthRequest;
import com.ege.wooda.global.security.oauth.ios.service.AppleAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class MemberController {
    private final JwtGenerateService jwtGenerateService;
    private final AppleAuthService appleAuthService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthTokenResponse>> generateAuthToken(
            @RequestBody @Validated AuthRequest authRequest) {
        AuthTokenResponse tokenData = AuthTokenResponse.of(
                jwtGenerateService.createJwtTokenByAuthToken(authRequest.authToken()));
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(MemberResponseMessage.CREATE_TOKEN_SUCCESS.getMessage(),
                                                  tokenData));
    }

    @PostMapping("/apple")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> handleAppleCallbackAndGenerateAuthToken(
            @RequestBody AppleAuthRequest appleResponse)
            throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException,
                   JsonProcessingException {
        AuthTokenResponse tokenData = AuthTokenResponse.of(
                jwtGenerateService.createJwtTokenByAppleAuth(appleAuthService.loadUser(appleResponse)));
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(MemberResponseMessage.CREATE_TOKEN_SUCCESS.getMessage(),
                                                  tokenData));
    }
}
