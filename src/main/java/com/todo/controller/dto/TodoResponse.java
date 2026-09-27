package com.todo.controller.dto;

import com.todo.domain.Status;
import com.todo.domain.Todo;

import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        String title,
        String description,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
