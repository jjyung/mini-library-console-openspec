package com.example.library.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(String code, String message, T data) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(BusinessCode.SUCCESS.getCode(), message, data);
    }

    public static <T> ApiResponse<T> failure(BusinessCode businessCode, String message) {
        return new ApiResponse<>(businessCode.getCode(), message, null);
    }
}
