package com.ege.wooda.global.common.response;

import lombok.*;

public record ApiResponse<T> (String status,
                              String message,
                              T data){

    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAIL_STATUS = "FAIL";

    @Builder
    public ApiResponse {}

    public static <T> ApiResponse<T> createSuccessWithData(String message, T data) {
        return new ApiResponse<>(SUCCESS_STATUS, message, data);
    }

    public static ApiResponse<?> createSuccess(String message) {
        return createSuccessWithData(message, null);
    }

    public static <T> ApiResponse<T> createFailWithData(String message, T data) {
        return new ApiResponse<>(FAIL_STATUS, message, data);
    }
    public static ApiResponse<?> createFail(String message) {
        return new ApiResponse<>(FAIL_STATUS, message, null);
    }

}
