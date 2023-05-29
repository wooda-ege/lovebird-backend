package com.ege.wooda.global.advice;

import com.ege.wooda.domain.diary.dto.response.DiaryResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ege.wooda.domain.diary.controller")
public class DiaryControllerAdvice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> notFoundExHandler(EntityNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.createFail(DiaryResponseMessage.FAIL_READ_DIARY.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> badRequestExHandler(BindingResult bindingResult){
        System.out.println(bindingResult.getFieldErrors());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createFailWithData(
                    DiaryResponseMessage.FAIL_READ_DIARY.getMessage(),
                    bindingResult.getFieldErrors()));
    }
}
