package com.todo.service;

import com.todo.domain.Status;
import com.todo.domain.Todo;
import com.todo.global.exception.RestApiException;
import com.todo.global.exception.TodoErrorCode;
import com.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService(todoRepository);
    }

    @Test
    @DisplayName("create 는 새 Todo 를 만들어 저장한다")
    void create_savesNewTodo() {
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Todo created = todoService.create("장보기", "설명");

        assertThat(created.getTitle()).isEqualTo("장보기");
        assertThat(created.getStatus()).isEqualTo(Status.PENDING);
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    @DisplayName("findAll 은 저장된 모든 Todo 를 반환한다")
    void findAll_returnsAllTodos() {
        Todo todo = Todo.create("장보기", "설명");
        when(todoRepository.findAll()).thenReturn(List.of(todo));

        List<Todo> all = todoService.findAll();

        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("findById 는 존재하는 Todo 를 반환한다")
    void findById_existingId_returnsTodo() {
        Todo todo = Todo.reconstitute(1L, "장보기", "설명", Status.PENDING, null, null);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        Todo found = todoService.findById(1L);

        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById 는 존재하지 않는 id 면 TODO_NOT_FOUND 예외를 던진다")
    void findById_missingId_throwsNotFound() {
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(999L))
                .isInstanceOf(RestApiException.class)
                .extracting(e -> ((RestApiException) e).getErrorCode())
                .isEqualTo(TodoErrorCode.TODO_NOT_FOUND);
    }

    @Test
    @DisplayName("update 는 제목과 설명을 변경 후 저장한다")
    void update_changesTitleAndDescription() {
        Todo todo = Todo.reconstitute(1L, "장보기", "설명", Status.PENDING, null, null);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Todo updated = todoService.update(1L, "빨래하기", "새 설명");

        assertThat(updated.getTitle()).isEqualTo("빨래하기");
        assertThat(updated.getDescription()).isEqualTo("새 설명");
    }

    @Test
    @DisplayName("update 는 존재하지 않는 id 면 TODO_NOT_FOUND 예외를 던진다")
    void update_missingId_throwsNotFound() {
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.update(999L, "제목", "설명"))
                .isInstanceOf(RestApiException.class)
                .extracting(e -> ((RestApiException) e).getErrorCode())
                .isEqualTo(TodoErrorCode.TODO_NOT_FOUND);
    }

    @Test
    @DisplayName("delete 는 존재하는 Todo 를 삭제한다")
    void delete_existingId_deletesTodo() {
        Todo todo = Todo.reconstitute(1L, "장보기", "설명", Status.PENDING, null, null);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        todoService.delete(1L);

        verify(todoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete 는 존재하지 않는 id 면 TODO_NOT_FOUND 예외를 던진다")
    void delete_missingId_throwsNotFound() {
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.delete(999L))
                .isInstanceOf(RestApiException.class)
                .extracting(e -> ((RestApiException) e).getErrorCode())
                .isEqualTo(TodoErrorCode.TODO_NOT_FOUND);
        verify(todoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("complete 는 상태를 COMPLETED 로 바꿔 저장한다")
    void complete_changesStatusToCompleted() {
        Todo todo = Todo.reconstitute(1L, "장보기", "설명", Status.PENDING, null, null);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Todo completed = todoService.complete(1L);

        assertThat(completed.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    @DisplayName("reopen 은 상태를 PENDING 으로 되돌려 저장한다")
    void reopen_changesStatusToPending() {
        Todo todo = Todo.reconstitute(1L, "장보기", "설명", Status.COMPLETED, null, null);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Todo reopened = todoService.reopen(1L);

        assertThat(reopened.getStatus()).isEqualTo(Status.PENDING);
    }
}
