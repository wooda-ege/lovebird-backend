package com.ege.wooda.global.advice;

import com.ege.wooda.domain.member.dto.response.MemberResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
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
                .body(ApiResponse.createFail(MemberResponseMessage.FAIL_READ_MEMBER.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> badRequestExHandler(BindingResult bindingResult) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createFailWithData(
                        MemberResponseMessage.FAIL_READ_MEMBER.getMessage(),
                        bindingResult.getFieldErrors()));
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiResponse<?>> duplicateExHandler(DataIntegrityViolationException exception) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.createFail(MemberResponseMessage.DUPLICATED_NICKNAME.getMessage()));
//    }
}
