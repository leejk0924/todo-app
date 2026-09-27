package com.todo.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TodoErrorCode implements ErrorCode {
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "제목은 비어 있을 수 없습니다."),
    TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "제목은 100자를 넘을 수 없습니다."),
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 할 일입니다.");
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
