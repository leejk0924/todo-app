package com.todo.controller;

import com.todo.controller.dto.TodoCreateRequest;
import com.todo.controller.dto.TodoResponse;
import com.todo.controller.dto.TodoUpdateRequest;
import com.todo.domain.Status;
import com.todo.domain.Todo;
import com.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public ResponseEntity<PagedModel<TodoResponse>> findAll(
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        var page = todoService.findAll(status, pageable).map(TodoResponse::from);
        return ResponseEntity.ok(new PagedModel<>(page));
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
