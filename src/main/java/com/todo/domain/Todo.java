package com.todo.domain;

import com.todo.global.exception.RestApiException;
import com.todo.global.exception.TodoErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Todo {

    private static final int MAX_TITLE_LENGTH = 100;

    private final Long id;
    private String title;
    private String description;
    private Status status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Todo(Long id, String title, String description, Status status,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Todo create(String title, String description) {
        validateTitle(title);
        LocalDateTime now = LocalDateTime.now();
        return new Todo(null, title, description, Status.PENDING, now, now);
    }

    public static Todo reconstitute(Long id, String title, String description, Status status,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Todo(id, title, description, status, createdAt, updatedAt);
    }

    public void changeTitle(String title) {
        validateTitle(title);
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = Status.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reopen() {
        this.status = Status.PENDING;
        this.updatedAt = LocalDateTime.now();
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new RestApiException(TodoErrorCode.INVALID_TITLE);
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new RestApiException(TodoErrorCode.TITLE_TOO_LONG);
        }
    }
}
