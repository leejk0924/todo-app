package com.todo.domain;

import com.todo.global.exception.RestApiException;
import com.todo.global.exception.TodoErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TodoTest {

    @Test
    @DisplayName("create 는 PENDING 상태의 새 Todo 를 만든다")
    void create_validTitle_createsPendingTodo() {
        Todo todo = Todo.create("장보기", "우유, 계란 사기");

        assertThat(todo.getId()).isNull();
        assertThat(todo.getTitle()).isEqualTo("장보기");
        assertThat(todo.getDescription()).isEqualTo("우유, 계란 사기");
        assertThat(todo.getStatus()).isEqualTo(Status.PENDING);
        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isEqualTo(todo.getCreatedAt());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("제목이 비어있거나 공백뿐이면 INVALID_TITLE 예외가 발생한다")
    void create_blankTitle_throwsInvalidTitle(String blankTitle) {
        assertThatThrownBy(() -> Todo.create(blankTitle, "설명"))
                .isInstanceOf(RestApiException.class)
                .extracting(e -> ((RestApiException) e).getErrorCode())
                .isEqualTo(TodoErrorCode.INVALID_TITLE);
    }

    @Test
    @DisplayName("제목이 최대 길이를 넘으면 TITLE_TOO_LONG 예외가 발생한다")
    void create_tooLongTitle_throwsTitleTooLong() {
        String tooLongTitle = "a".repeat(101);

        assertThatThrownBy(() -> Todo.create(tooLongTitle, "설명"))
                .isInstanceOf(RestApiException.class)
                .extracting(e -> ((RestApiException) e).getErrorCode())
                .isEqualTo(TodoErrorCode.TITLE_TOO_LONG);
    }

    @Test
    @DisplayName("최대 길이와 정확히 같은 제목은 허용한다")
    void create_maxLengthTitle_succeeds() {
        String maxLengthTitle = "a".repeat(100);

        Todo todo = Todo.create(maxLengthTitle, "설명");

        assertThat(todo.getTitle()).isEqualTo(maxLengthTitle);
    }

    @Test
    @DisplayName("changeTitle 은 제목과 updatedAt 을 갱신한다")
    void changeTitle_validTitle_updatesTitleAndUpdatedAt() {
        Todo todo = Todo.create("장보기", "설명");
        LocalDateTime beforeUpdatedAt = todo.getUpdatedAt();

        todo.changeTitle("빨래하기");

        assertThat(todo.getTitle()).isEqualTo("빨래하기");
        assertThat(todo.getUpdatedAt()).isAfterOrEqualTo(beforeUpdatedAt);
    }

    @Test
    @DisplayName("changeTitle 에 빈 제목을 주면 예외가 발생하고 기존 제목은 유지된다")
    void changeTitle_blankTitle_throwsAndKeepsOriginal() {
        Todo todo = Todo.create("장보기", "설명");

        assertThatThrownBy(() -> todo.changeTitle(" "))
                .isInstanceOf(RestApiException.class);
        assertThat(todo.getTitle()).isEqualTo("장보기");
    }

    @Test
    @DisplayName("changeDescription 은 설명을 갱신한다")
    void changeDescription_updatesDescription() {
        Todo todo = Todo.create("장보기", "설명");

        todo.changeDescription("새로운 설명");

        assertThat(todo.getDescription()).isEqualTo("새로운 설명");
    }

    @Test
    @DisplayName("complete 는 상태를 COMPLETED 로 바꾼다")
    void complete_changesStatusToCompleted() {
        Todo todo = Todo.create("장보기", "설명");

        todo.complete();

        assertThat(todo.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    @DisplayName("reopen 은 상태를 PENDING 으로 되돌린다")
    void reopen_changesStatusToPending() {
        Todo todo = Todo.create("장보기", "설명");
        todo.complete();

        todo.reopen();

        assertThat(todo.getStatus()).isEqualTo(Status.PENDING);
    }

    @Test
    @DisplayName("reconstitute 는 저장된 값 그대로 Todo 를 복원한다")
    void reconstitute_restoresExactState() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 1, 2, 0, 0);

        Todo todo = Todo.reconstitute(1L, "장보기", "설명", Status.DOING, createdAt, updatedAt);

        assertThat(todo.getId()).isEqualTo(1L);
        assertThat(todo.getTitle()).isEqualTo("장보기");
        assertThat(todo.getDescription()).isEqualTo("설명");
        assertThat(todo.getStatus()).isEqualTo(Status.DOING);
        assertThat(todo.getCreatedAt()).isEqualTo(createdAt);
        assertThat(todo.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
