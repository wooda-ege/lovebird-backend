package com.ege.wooda.global.advice;

import com.ege.wooda.domain.calendar.dto.response.CalendarResponseMessage;
import com.ege.wooda.global.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ege.wooda.domain.calendar.controller")
public class CalendarControllerAdvice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> notFoundExHandler(EntityNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.createFail(CalendarResponseMessage.FAIL_READ_CALENDAR.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> badRequestExHandler(BindingResult bindingResult) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createFailWithData(
                        CalendarResponseMessage.FAIL_READ_CALENDAR.getMessage(),
                        bindingResult.getFieldErrors()
                ));
    }
}
