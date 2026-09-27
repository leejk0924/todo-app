package com.todo.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TodoErrorCode implements ErrorCode {
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "사용할 수 없는 제목입니다.");
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
        return "todo";
    }
}
