package com.todo.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus status() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String domain() {
        return "common";
    }
}
