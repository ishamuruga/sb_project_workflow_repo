# Copilot Instructions for Todo Application

## Project Overview

This is a **Spring Boot REST API** application for managing todos. The project uses:
- **Spring Boot 3.3.0** with Java 17+
- **Spring Data JPA** for data persistence
- **H2 Database** (file-based at `./data/tododb`)
- **Maven** for build automation
- **JUnit 5 & Mockito** for unit testing
- **GitHub Actions** for CI/CD

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/todo/
│   │   ├── TodoApplication.java           # Main Spring Boot entry point
│   │   ├── controller/
│   │   │   └── TodoController.java        # REST endpoints (@RestController)
│   │   ├── service/
│   │   │   └── TodoService.java           # Business logic (@Service)
│   │   ├── repository/
│   │   │   └── TodoRepository.java        # Data access (JpaRepository)
│   │   ├── model/
│   │   │   └── Todo.java                  # JPA Entity (@Entity)
│   │   ├── dto/
│   │   │   └── CreateTodoRequest.java     # Request DTOs
│   │   ├── config/
│   │   │   └── DataInitializer.java       # CommandLineRunner for seed data
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java # @RestControllerAdvice
│   └── resources/
│       └── application.properties          # Spring configuration
└── test/
    └── java/com/example/todo/
        └── controller/
            └── TodoControllerTest.java     # @WebMvcTest unit tests
```

---

## Coding Conventions

### 1. **Layer Architecture**

Follow the **3-tier architecture**:
- **Controller** (`@RestController`): Handles HTTP requests, input validation, logging
- **Service** (`@Service`): Contains business logic, transactional operations
- **Repository** (`JpaRepository`): Data access and persistence
- **Model** (`@Entity`): JPA entities with `@PrePersist` / `@PreUpdate` hooks

### 2. **REST API Design**

**Endpoint Pattern:** `/api/{resource}`

Current endpoints:
- `POST /api/todos` → Create a new todo (201 Created)
- `GET /api/todos` → Retrieve all todos (200 OK)

**For new endpoints, follow:**
- `GET /api/todos/{id}` → Get single todo
- `PUT /api/todos/{id}` → Update todo
- `DELETE /api/todos/{id}` → Delete todo
- Return appropriate HTTP status codes (200, 201, 400, 404, 500)

### 3. **Entity & DTO Design**

**Entity (`@Entity`):**
- Use Lombok: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Add validation: `@NotBlank`, `@NotNull` on fields
- Include audit fields: `createdAt`, `updatedAt` with `@PrePersist` / `@PreUpdate`
- Use `@Column` for database constraints

**Request DTO:**
- Suffix with `Request` (e.g., `CreateTodoRequest`)
- Use Lombok `@Data` for getters/setters
- Include validation annotations (`@NotBlank`, etc.)
- Keep DTOs lightweight

### 4. **Exception Handling**

All exceptions are caught by `GlobalExceptionHandler` (`@RestControllerAdvice`):
- **`@ExceptionHandler(MethodArgumentNotValidException.class)`** → `400 Bad Request` with field errors
- **`@ExceptionHandler(HttpMediaTypeNotSupportedException.class)`** → `415 Unsupported Media Type`
- **`@ExceptionHandler(Exception.class)`** → `500 Internal Server Error`

**For new exceptions:**
- Use `log.debug()` instead of `log.error()` for expected/handled exceptions
- Use `log.error()` only for truly unexpected system failures

### 5. **Service Layer**

- Mark query methods with `@Transactional(readOnly = true)`
- Mark write operations with `@Transactional` (default read-write)
- Log important operations at `INFO` level
- Log detailed debug info at `DEBUG` level

Example:
```java
@Transactional
public Todo createTodo(CreateTodoRequest request) {
    log.info("Creating todo with title: {}", request.getTitle());
    // business logic
    return saved;
}

@Transactional(readOnly = true)
public List<Todo> getAllTodos() {
    log.info("Fetching all todos");
    return todoRepository.findAll();
}
```

### 6. **Testing**

- Use `@WebMvcTest(TodoController.class)` for controller unit tests
- Mock the service layer with `@MockBean`
- Test **happy path** and **edge cases**
- Verify HTTP status codes, response body, and service interactions

**Test structure:**
```java
@WebMvcTest(TodoController.class)
class TodoControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean TodoService todoService;
    
    @Nested
    @DisplayName("POST /api/todos")
    class CreateTodo { /* tests */ }
    
    @Nested
    @DisplayName("GET /api/todos")
    class GetAllTodos { /* tests */ }
}
```

---

## Database Configuration

**H2 Setup** (`application.properties`):
```properties
spring.datasource.url=jdbc:h2:file:./data/tododb;AUTO_SERVER=TRUE
spring.jpa.hibernate.ddl-auto=update
```

- **DDL Auto**: `update` (creates/modifies tables automatically)
- **H2 Console**: Available at `http://localhost:8080/h2-console`
- **Data Init**: `DataInitializer` seeds 5 sample todos on startup

---

## Build & Run

**Build:**
```bash
mvn clean package
```

**Run:**
```bash
mvn spring-boot:run
```

**Test:**
```bash
mvn test
```

**CI/CD:** Configured in `.github/workflows/build.yml`
- Runs on: `main`, `develop`, `feature/**` branches
- Steps: Checkout → Setup Java 21 → Run tests → Package JAR

---

## Key Dependencies

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | REST API support |
| `spring-boot-starter-data-jpa` | ORM & repository pattern |
| `h2` | In-memory/file-based database |
| `spring-boot-starter-validation` | Bean validation (`@Valid`, `@NotBlank`) |
| `lombok` | Reduce boilerplate (getters, setters, builders) |
| `spring-boot-starter-test` | JUnit 5, Mockito, MockMvc |

---

## Logging

Configure logging level in `application.properties`:
```properties
logging.level.com.example.todo=DEBUG   # App-specific
logging.level.org.springframework=INFO # Spring framework
```

- **INFO**: Important business events (create, retrieve, update, delete)
- **DEBUG**: Detailed diagnostic info
- **ERROR**: Only for truly unexpected failures (in real scenarios, not tests)

---

## Future Enhancements

- Add `GET /api/todos/{id}` endpoint with exception handling
- Add `PUT /api/todos/{id}` for updates
- Add `DELETE /api/todos/{id}` for deletion
- Add filtering/pagination to `GET /api/todos`
- Add Spring Security for authentication
- Add OpenAPI (Swagger) documentation
- Add integration tests with `@SpringBootTest`

---

## Code Quality Standards

- **Naming**: Use clear, descriptive names (avoid abbreviations)
- **Method length**: Keep methods focused (< 20 lines where possible)
- **Comments**: Add comments for **why**, not **what**
- **Errors**: Always include meaningful error messages
- **Tests**: Maintain > 80% code coverage
- **Formatting**: Use IDE auto-formatting (spaces, indentation)

---

## Questions for Copilot

When asking Copilot to help with this project, specify:
1. **Layer**: Controller, Service, Repository, or Entity?
2. **Type**: New endpoint, bug fix, test case, or refactor?
3. **Scope**: Single method or entire class?
4. **Requirements**: Input validation, error handling, logging?

Example:
> "Add a `PUT /api/todos/{id}` endpoint in TodoController to update a todo. Validate that the title is not blank. The service should return 404 if the todo doesn't exist."

---

**Last Updated:** June 3, 2026  
**Java Version:** 17+  
**Spring Boot Version:** 3.3.0
