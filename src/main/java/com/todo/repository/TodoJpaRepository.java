package com.todo.repository;

import com.todo.domain.Status;
import com.todo.repository.entity.TodoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoJpaRepository extends JpaRepository<TodoEntity, Long> {
    Page<TodoEntity> findByStatus(Status status, Pageable pageable);
}
