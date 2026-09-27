package com.todo.repository;

import com.todo.domain.Status;
import com.todo.domain.Todo;
import com.todo.global.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class TodoRepositoryImplTest {

    @Autowired
    private TodoJpaRepository todoJpaRepository;

    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        this.todoRepository = new TodoRepositoryImpl(todoJpaRepository);
    }

    @Test
    @DisplayName("save 는 새 Todo 를 저장하고 id/생성시각/수정시각을 채워 돌려준다")
    void save_newTodo_persistsAndFillsGeneratedFields() {
        Todo todo = Todo.create("장보기", "우유, 계란 사기");

        Todo saved = todoRepository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("장보기");
    }

    @Test
    @DisplayName("save 는 기존 Todo 의 변경 사항을 반영하고 createdAt 을 유지한다")
    void save_existingTodo_updatesFieldsAndKeepsCreatedAt() {
        Todo saved = todoRepository.save(Todo.create("장보기", "설명"));
        var beforeCreatedAt = saved.getCreatedAt();
        var beforeUpdatedAt = saved.getUpdatedAt();

        saved.changeTitle("빨래하기");
        saved.complete();
        Todo updated = todoRepository.save(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getTitle()).isEqualTo("빨래하기");
        assertThat(updated.getStatus()).isEqualTo(Status.COMPLETED);
        assertThat(updated.getCreatedAt()).isEqualTo(beforeCreatedAt);
        assertThat(updated.getUpdatedAt()).isAfter(beforeUpdatedAt);
    }

    @Test
    @DisplayName("findById 는 존재하지 않는 id 에 대해 빈 Optional 을 반환한다")
    void findById_missingId_returnsEmpty() {
        Optional<Todo> found = todoRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll 은 status 가 없으면 페이지 크기만큼 저장된 모든 Todo 를 반환한다")
    void findAll_noStatusFilter_returnsPagedTodos() {
        todoRepository.save(Todo.create("장보기", "설명1"));
        todoRepository.save(Todo.create("빨래하기", "설명2"));
        todoRepository.save(Todo.create("청소하기", "설명3"));

        Page<Todo> page = todoRepository.findAll(null, PageRequest.of(0, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("findAll 은 status 로 필터링한 Todo 만 반환한다")
    void findAll_withStatusFilter_returnsOnlyMatchingStatus() {
        Todo completed = todoRepository.save(Todo.create("장보기", "설명1"));
        completed.complete();
        todoRepository.save(completed);
        todoRepository.save(Todo.create("빨래하기", "설명2"));

        Page<Todo> page = todoRepository.findAll(Status.COMPLETED, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    @DisplayName("deleteById 는 해당 Todo 를 삭제한다")
    void deleteById_removesTodo() {
        Todo saved = todoRepository.save(Todo.create("장보기", "설명"));

        todoRepository.deleteById(saved.getId());

        assertThat(todoRepository.existsById(saved.getId())).isFalse();
    }
}
