package com.todo.repository;

import com.todo.domain.Status;
import com.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);

    Optional<Todo> findById(Long id);

    Page<Todo> findAll(Status status, Pageable pageable);

    void deleteById(Long id);

    boolean existsById(Long id);
}
