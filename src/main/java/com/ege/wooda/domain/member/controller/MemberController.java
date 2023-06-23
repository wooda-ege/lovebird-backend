package com.ege.wooda.domain.member.controller;

import com.ege.wooda.domain.member.dto.request.AuthRequest;
import com.ege.wooda.domain.member.dto.response.AuthTokenResponse;
import com.ege.wooda.domain.member.dto.response.MemberResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;
import com.ege.wooda.global.security.jwt.service.JwtGenerateService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class MemberController {
    private final JwtGenerateService jwtGenerateService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthTokenResponse>> authToken(
            @RequestBody @Validated AuthRequest authRequest) {
        AuthTokenResponse tokenData = AuthTokenResponse.of(
                jwtGenerateService.createJwtTokenByAuthToken(authRequest.authToken()));
        return ResponseEntity.ok(
                ApiResponse.createSuccessWithData(MemberResponseMessage.CREATE_TOKEN_SUCCESS.getMessage(),
                                                  tokenData));
    }
}
