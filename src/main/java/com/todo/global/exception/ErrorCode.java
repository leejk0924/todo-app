package com.todo.global.exception;

import org.springframework.http.HttpStatus;

import java.net.URI;

public interface ErrorCode {
    String name();

    HttpStatus status();

    String getMessage();

    String domain();

    default URI type() {
        String slug = name().toLowerCase().replace('_', '-');
        return URI.create("urn:problem-type:" + domain() + ":" + slug);
    }
}
