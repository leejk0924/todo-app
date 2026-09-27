package com.todo.controller;

import tools.jackson.databind.ObjectMapper;
import com.todo.controller.dto.TodoCreateRequest;
import com.todo.controller.dto.TodoUpdateRequest;
import com.todo.domain.Todo;
import com.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("POST /api/todos - 유효한 요청이면 201 과 생성된 Todo 를 반환한다")
    void create_validRequest_returns201() throws Exception {
        TodoCreateRequest request = new TodoCreateRequest("장보기", "우유, 계란 사기");

        mockMvc.perform(post("/api/todos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("장보기"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/todos - 제목이 공백이면 400 과 에러코드를 반환한다")
    void create_blankTitle_returns400() throws Exception {
        TodoCreateRequest request = new TodoCreateRequest(" ", "설명");

        mockMvc.perform(post("/api/todos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_TITLE"));
    }

    @Test
    @DisplayName("POST /api/todos - 제목이 100자를 넘으면 400 과 에러코드를 반환한다")
    void create_tooLongTitle_returns400() throws Exception {
        TodoCreateRequest request = new TodoCreateRequest("a".repeat(101), "설명");

        mockMvc.perform(post("/api/todos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("TITLE_TOO_LONG"));
    }

    @Test
    @DisplayName("GET /api/todos - 저장된 Todo 목록을 반환한다")
    void findAll_returnsAllTodos() throws Exception {
        todoRepository.save(Todo.create("장보기", "설명1"));
        todoRepository.save(Todo.create("빨래하기", "설명2"));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/todos/{id} - 존재하는 id 면 200 과 Todo 를 반환한다")
    void findById_existingId_returns200() throws Exception {
        Todo saved = todoRepository.save(Todo.create("장보기", "설명"));

        mockMvc.perform(get("/api/todos/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("장보기"));
    }

    @Test
    @DisplayName("GET /api/todos/{id} - 존재하지 않는 id 면 404 와 에러코드를 반환한다")
    void findById_missingId_returns404() throws Exception {
        mockMvc.perform(get("/api/todos/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TODO_NOT_FOUND"));
    }

    @Test
    @DisplayName("PUT /api/todos/{id} - 존재하는 id 면 수정 후 200 을 반환한다")
    void update_existingId_returns200() throws Exception {
        Todo saved = todoRepository.save(Todo.create("장보기", "설명"));
        TodoUpdateRequest request = new TodoUpdateRequest("빨래하기", "새 설명");

        mockMvc.perform(put("/api/todos/{id}", saved.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("빨래하기"))
                .andExpect(jsonPath("$.description").value("새 설명"));
    }

    @Test
    @DisplayName("PUT /api/todos/{id} - 존재하지 않는 id 면 404 를 반환한다")
    void update_missingId_returns404() throws Exception {
        TodoUpdateRequest request = new TodoUpdateRequest("빨래하기", "새 설명");

        mockMvc.perform(put("/api/todos/{id}", 999999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TODO_NOT_FOUND"));
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} - 존재하는 id 면 204 를 반환하고 실제로 삭제된다")
    void delete_existingId_returns204() throws Exception {
        Todo saved = todoRepository.save(Todo.create("장보기", "설명"));

        mockMvc.perform(delete("/api/todos/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/todos/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} - 존재하지 않는 id 면 404 를 반환한다")
    void delete_missingId_returns404() throws Exception {
        mockMvc.perform(delete("/api/todos/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TODO_NOT_FOUND"));
    }

    @Test
    @DisplayName("PATCH /api/todos/{id}/complete - 상태를 COMPLETED 로 바꾼다")
    void complete_existingId_returns200() throws Exception {
        Todo saved = todoRepository.save(Todo.create("장보기", "설명"));

        mockMvc.perform(patch("/api/todos/{id}/complete", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("PATCH /api/todos/{id}/reopen - 상태를 PENDING 으로 되돌린다")
    void reopen_existingId_returns200() throws Exception {
        Todo saved = todoRepository.save(Todo.create("장보기", "설명"));
        todoRepository.save(saved);
        mockMvc.perform(patch("/api/todos/{id}/complete", saved.getId()));

        mockMvc.perform(patch("/api/todos/{id}/reopen", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
