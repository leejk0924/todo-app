package com.todo.repository.entity;

import com.todo.domain.Status;
import com.todo.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(length = 500)
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    public TodoEntity(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }
}
