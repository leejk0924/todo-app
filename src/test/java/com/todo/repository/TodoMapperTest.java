package com.todo.repository;

import com.todo.domain.Status;
import com.todo.domain.Todo;
import com.todo.repository.entity.TodoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TodoMapperTest {

    @Test
    @DisplayName("toNewEntity 는 id/시각 없이 title, description, status 만 옮긴다")
    void toNewEntity_copiesEditableFieldsOnly() {
        Todo todo = Todo.create("장보기", "우유, 계란 사기");

        TodoEntity entity = TodoMapper.toNewEntity(todo);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getTitle()).isEqualTo("장보기");
        assertThat(entity.getDescription()).isEqualTo("우유, 계란 사기");
        assertThat(entity.getStatus()).isEqualTo(Status.PENDING);
    }

    @Test
    @DisplayName("toDomain 은 entity 의 값을 그대로 도메인으로 복원한다")
    void toDomain_restoresExactState() {
        TodoEntity entity = TodoEntity.builder()
                .title("장보기")
                .description("설명")
                .status(Status.DOING)
                .build();
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 1, 2, 0, 0);
        ReflectionTestUtils.setField(entity, "id", 1L);
        ReflectionTestUtils.setField(entity, "createdAt", createdAt);
        ReflectionTestUtils.setField(entity, "updatedAt", updatedAt);

        Todo todo = TodoMapper.toDomain(entity);

        assertThat(todo.getId()).isEqualTo(1L);
        assertThat(todo.getTitle()).isEqualTo("장보기");
        assertThat(todo.getDescription()).isEqualTo("설명");
        assertThat(todo.getStatus()).isEqualTo(Status.DOING);
        assertThat(todo.getCreatedAt()).isEqualTo(createdAt);
        assertThat(todo.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
