package com.todo.repository;

import com.todo.domain.Todo;
import com.todo.repository.entity.TodoEntity;

public class TodoMapper {

    private TodoMapper() {
    }

    public static TodoEntity toNewEntity(Todo todo) {
        return TodoEntity.builder()
                .title(todo.getTitle())
                .description(todo.getDescription())
                .status(todo.getStatus())
                .build();
    }

    public static Todo toDomain(TodoEntity entity) {
        return Todo.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
