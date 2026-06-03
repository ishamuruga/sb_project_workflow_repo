package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public Todo createTodo(CreateTodoRequest request) {
        log.info("Creating todo with title: {}", request.getTitle());
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .completed(false)
                .build();
        Todo saved = todoRepository.save(todo);
        log.info("Created todo with id: {}, priority: {}", saved.getId(), saved.getPriority());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Todo> getAllTodos() {
        log.info("Fetching all todos");
        return todoRepository.findAll();
    }
}
