package com.todo.service;

import com.todo.domain.Status;
import com.todo.domain.Todo;
import com.todo.global.exception.RestApiException;
import com.todo.global.exception.TodoErrorCode;
import com.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public Todo create(String title, String description) {
        Todo todo = Todo.create(title, description);
        return todoRepository.save(todo);
    }

    public Page<Todo> findAll(Status status, Pageable pageable) {
        return todoRepository.findAll(status, pageable);
    }

    public Todo findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new RestApiException(TodoErrorCode.TODO_NOT_FOUND));
    }

    @Transactional
    public Todo update(Long id, String title, String description) {
        Todo todo = findById(id);
        todo.changeTitle(title);
        todo.changeDescription(description);
        return todoRepository.save(todo);
    }

    @Transactional
    public void delete(Long id) {
        Todo todo = findById(id);
        todoRepository.deleteById(todo.getId());
    }

    @Transactional
    public Todo complete(Long id) {
        Todo todo = findById(id);
        todo.complete();
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo reopen(Long id) {
        Todo todo = findById(id);
        todo.reopen();
        return todoRepository.save(todo);
    }
}
