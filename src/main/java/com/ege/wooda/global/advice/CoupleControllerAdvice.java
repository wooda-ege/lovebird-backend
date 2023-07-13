package com.ege.wooda.global.advice;

import com.ege.wooda.domain.couple.dto.response.CoupleResponseMessage;
import com.ege.wooda.domain.couple.exception.CodeExpiredException;
import com.ege.wooda.global.common.response.ApiResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = { "com.ege.wooda.domain.couple.controller" })
public class CoupleControllerAdvice {

    @ExceptionHandler(FirebaseMessagingException.class)
    public ResponseEntity<ApiResponse<?>> firebaseExHandler(FirebaseMessagingException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createFail(CoupleResponseMessage.SEND_CODE_FAIL.getMessage()));
    }

    @ExceptionHandler(CodeExpiredException.class)
    public ResponseEntity<ApiResponse<?>> codeExpiredExHandler(CodeExpiredException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createFail(CoupleResponseMessage.CODE_EXPIRED.getMessage()));
    }
}
