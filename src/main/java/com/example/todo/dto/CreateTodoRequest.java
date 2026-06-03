package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTodoRequest {

    @NotBlank(message = "Title must not be blank")
    private String title;

    private String description;
}
