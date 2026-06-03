package com.example.todo.config;

import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository todoRepository;

    @Override
    public void run(String... args) {
        log.info("Seeding initial todo records...");

        List<Todo> sampleTodos = List.of(
                Todo.builder()
                        .title("Buy groceries")
                        .description("Milk, eggs, bread, and coffee")
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Read a book")
                        .description("Finish reading 'Clean Code' by Robert C. Martin")
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Go for a morning run")
                        .description("30-minute run in the park")
                        .completed(true)
                        .build(),
                Todo.builder()
                        .title("Write unit tests")
                        .description("Add unit tests for the Todo service and controller")
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Review pull requests")
                        .description("Review open PRs in the team repository")
                        .completed(true)
                        .build()
        );

        List<Todo> saved = todoRepository.saveAll(sampleTodos);
        log.info("Seeded {} todo records successfully.", saved.size());
        saved.forEach(t -> log.info("  -> [{}] {} (completed={})", t.getId(), t.getTitle(), t.isCompleted()));
    }
}
