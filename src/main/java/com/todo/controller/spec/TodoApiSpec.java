package com.todo.controller.spec;

import com.todo.controller.dto.TodoCreateRequest;
import com.todo.controller.dto.TodoResponse;
import com.todo.controller.dto.TodoUpdateRequest;
import com.todo.domain.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;

@Tag(name = "할 일", description = "할 일(Todo) 관련 API")
public interface TodoApiSpec {

    @Operation(summary = "할 일 생성", description = "제목이 비어있거나 100자를 넘으면 400을 반환합니다.")
    ResponseEntity<TodoResponse> create(TodoCreateRequest request);

    @Operation(summary = "할 일 목록 조회", description = "완료 여부(status)로 필터링하고 페이지 단위로 조회합니다.")
    ResponseEntity<PagedModel<TodoResponse>> findAll(Status status, Pageable pageable);

    @Operation(summary = "할 일 단건 조회", description = "존재하지 않는 id 면 404를 반환합니다.")
    ResponseEntity<TodoResponse> findById(Long id);

    @Operation(summary = "할 일 수정", description = "존재하지 않는 id 면 404, 제목이 유효하지 않으면 400을 반환합니다.")
    ResponseEntity<TodoResponse> update(Long id, TodoUpdateRequest request);

    @Operation(summary = "할 일 삭제", description = "존재하지 않는 id 면 404를 반환합니다.")
    ResponseEntity<Void> delete(Long id);

    @Operation(summary = "할 일 완료 처리")
    ResponseEntity<TodoResponse> complete(Long id);

    @Operation(summary = "할 일 완료 취소(재개)")
    ResponseEntity<TodoResponse> reopen(Long id);
}
