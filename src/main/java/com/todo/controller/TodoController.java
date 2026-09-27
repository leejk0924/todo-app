package com.todo.controller;

import com.todo.controller.dto.TodoCreateRequest;
import com.todo.controller.dto.TodoResponse;
import com.todo.controller.dto.TodoUpdateRequest;
import com.todo.domain.Todo;
import com.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponse> create(@RequestBody TodoCreateRequest request) {
        Todo todo = todoService.create(request.title(), request.description());
        return ResponseEntity.created(URI.create("/api/todos/" + todo.getId()))
                .body(TodoResponse.from(todo));
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> findAll() {
        List<TodoResponse> responses = todoService.findAll().stream()
                .map(TodoResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> findById(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        return ResponseEntity.ok(TodoResponse.from(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@PathVariable Long id, @RequestBody TodoUpdateRequest request) {
        Todo todo = todoService.update(id, request.title(), request.description());
        return ResponseEntity.ok(TodoResponse.from(todo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> complete(@PathVariable Long id) {
        Todo todo = todoService.complete(id);
        return ResponseEntity.ok(TodoResponse.from(todo));
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<TodoResponse> reopen(@PathVariable Long id) {
        Todo todo = todoService.reopen(id);
        return ResponseEntity.ok(TodoResponse.from(todo));
    }
}
