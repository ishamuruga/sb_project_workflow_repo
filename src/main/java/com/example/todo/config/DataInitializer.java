package com.example.todo.config;

import com.example.todo.model.Priority;
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
                        .priority(Priority.HIGH)
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Read a book")
                        .description("Finish reading 'Clean Code' by Robert C. Martin")
                        .priority(Priority.MEDIUM)
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Go for a morning run")
                        .description("30-minute run in the park")
                        .priority(Priority.LOW)
                        .completed(true)
                        .build(),
                Todo.builder()
                        .title("Write unit tests")
                        .description("Add unit tests for the Todo service and controller")
                        .priority(Priority.HIGH)
                        .completed(false)
                        .build(),
                Todo.builder()
                        .title("Review pull requests")
                        .description("Review open PRs in the team repository")
                        .priority(Priority.MEDIUM)
                        .completed(true)
                        .build()
        );

        List<Todo> saved = todoRepository.saveAll(sampleTodos);
        log.info("Seeded {} todo records successfully.", saved.size());
        saved.forEach(t -> log.info("  -> [{}] {} (priority={}, completed={})", t.getId(), t.getTitle(), t.getPriority(), t.isCompleted()));
    }
}
