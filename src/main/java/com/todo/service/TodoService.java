package com.todo.service;

import com.todo.domain.Todo;
import com.todo.global.exception.RestApiException;
import com.todo.global.exception.TodoErrorCode;
import com.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Todo> findAll() {
        return todoRepository.findAll();
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
