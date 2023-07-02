package com.ege.wooda.global.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ege.wooda.domain.profile.dto.response.ProfileResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice(basePackages = { "com.ege.wooda.domain.profile.controller" })
public class ProfileControllerAdvice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> notFoundExHandler(EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.createFail(ProfileResponseMessage.READ_PROFILE_FAIL.getMessage()));
    }


//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiResponse<?>> duplicateExHandler(DataIntegrityViolationException exception) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.createFail(MemberResponseMessage.DUPLICATED_NICKNAME.getMessage()));
//    }
}
