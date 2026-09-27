package com.todo.repository;

import com.todo.domain.Todo;
import com.todo.global.exception.RestApiException;
import com.todo.global.exception.TodoErrorCode;
import com.todo.repository.entity.TodoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepository {

    private final TodoJpaRepository todoJpaRepository;

    @Override
    public Todo save(Todo todo) {
        TodoEntity entity = (todo.getId() == null) ? insert(todo) : update(todo);
        return TodoMapper.toDomain(entity);
    }

    private TodoEntity insert(Todo todo) {
        TodoEntity entity = TodoMapper.toNewEntity(todo);
        return todoJpaRepository.save(entity);
    }

    private TodoEntity update(Todo todo) {
        TodoEntity entity = todoJpaRepository.findById(todo.getId())
                .orElseThrow(() -> new RestApiException(TodoErrorCode.TODO_NOT_FOUND));
        entity.changeTitle(todo.getTitle());
        entity.changeDescription(todo.getDescription());
        entity.changeStatus(todo.getStatus());
        return entity;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoJpaRepository.findById(id).map(TodoMapper::toDomain);
    }

    @Override
    public List<Todo> findAll() {
        return todoJpaRepository.findAll().stream()
                .map(TodoMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        todoJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return todoJpaRepository.existsById(id);
    }
}
