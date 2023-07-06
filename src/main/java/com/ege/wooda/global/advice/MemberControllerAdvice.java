package com.ege.wooda.global.advice;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.naming.AuthenticationException;

import com.ege.wooda.domain.member.dto.response.MemberResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = { "com.ege.wooda.domain.member.controller" })
public class MemberControllerAdvice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> notFoundExHandler(EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.createFail(MemberResponseMessage.CREATE_TOKEN_FAIL.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> badRequestExHandler(BindingResult bindingResult) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createFailWithData(
                        MemberResponseMessage.CREATE_TOKEN_FAIL.getMessage(),
                        bindingResult.getFieldErrors()));
    }

    @ExceptionHandler({
            JsonProcessingException.class, AuthenticationException.class, NoSuchAlgorithmException.class,
            InvalidKeySpecException.class
    })
    public ResponseEntity<ApiResponse<?>> appleAuthExHandler() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED).
                body(ApiResponse.createFail(MemberResponseMessage.FAIL_AUTHORIZATION_APPLE.getMessage()));
    }
}
