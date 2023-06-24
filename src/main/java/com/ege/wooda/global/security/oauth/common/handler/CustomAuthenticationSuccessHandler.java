package com.ege.wooda.global.security.oauth.common.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ege.wooda.domain.member.dto.response.MemberResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;
import com.ege.wooda.global.security.jwt.service.JwtGenerateService;
import com.ege.wooda.global.security.oauth.model.PrincipalUser;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtGenerateService jwtGenerateService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        String authToken = jwtGenerateService.createAuthToken(principalUser);


        response.getWriter().write(authToken);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
