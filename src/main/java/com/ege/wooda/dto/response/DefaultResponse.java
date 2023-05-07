package com.ege.wooda.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DefaultResponse<T> {
    private int statusCode;
    private String responseMessage;
    private T data;

    public DefaultResponse(final int statusCode, final String responseMessage) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.data=null;
    }

    public static<T> DefaultResponse<T> response(final int statusCode, final String responseMessage){
        return response(statusCode, responseMessage, null);
    }

    public static<T> DefaultResponse<T> response(final int statusCode, final String responseMessage, final T t){
        return DefaultResponse.<T>builder()
                .data(t)
                .statusCode(statusCode)
                .responseMessage(responseMessage)
                .build();
    }
}
