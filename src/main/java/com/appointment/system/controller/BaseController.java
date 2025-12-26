package com.appointment.system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.appointment.system.dto.Responses.ApiResponse;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> ok(T result, String message) {
        ApiResponse<T> r = new ApiResponse<>(true, result, HttpStatus.OK.value(), message, null);
        return ResponseEntity.ok(r);
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(T result, String message) {
        ApiResponse<T> r = new ApiResponse<>(true, result, HttpStatus.CREATED.value(), message, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(r);
    }

    protected <T> ResponseEntity<ApiResponse<T>> error(int status, String message, String errorCode) {
        ApiResponse<T> r = new ApiResponse<>(false, null, status, message, errorCode);
        return ResponseEntity.status(status).body(r);
    }
}