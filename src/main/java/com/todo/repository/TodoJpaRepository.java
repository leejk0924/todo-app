package com.todo.repository;

import com.todo.repository.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoJpaRepository extends JpaRepository<TodoEntity, Long> {
}
