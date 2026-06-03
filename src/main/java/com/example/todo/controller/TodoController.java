package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List

@Slf4j
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * POST /api/todos
     * Create a new todo item.
     */
    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody CreateTodoRequest request) {
        log.info("POST /api/todos - Creating todo: {}", request.getTitle());
        Todo created = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/todos
     * Retrieve all todo items.
     */
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        log.info("GET /api/todos - Fetching all todos");
        List<Todo> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    Test123
}
