package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@DisplayName("TodoController Unit Tests")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo sampleTodo;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        fixedTime = LocalDateTime.of(2026, 6, 3, 10, 0, 0);
        sampleTodo = Todo.builder()
                .id(1L)
                .title("Buy groceries")
                .description("Milk, eggs, bread")
                .priority(Priority.HIGH)
                .completed(false)
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/todos
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/todos - Create Todo")
    class CreateTodo {

        @Test
        @DisplayName("201 Created - valid request with title and description")
        void createTodo_validRequest_returns201() throws Exception {
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("Buy groceries");
            request.setDescription("Milk, eggs, bread");

            when(todoService.createTodo(any(CreateTodoRequest.class))).thenReturn(sampleTodo);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("Buy groceries"))
                    .andExpect(jsonPath("$.description").value("Milk, eggs, bread"))
                    .andExpect(jsonPath("$.priority").value("HIGH"))
                    .andExpect(jsonPath("$.completed").value(false));

            verify(todoService, times(1)).createTodo(any(CreateTodoRequest.class));
        }

        @Test
        @DisplayName("201 Created - valid request with title only (no description)")
        void createTodo_titleOnlyNoDescription_returns201() throws Exception {
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("Read a book");

            Todo todoWithoutDesc = Todo.builder()
                    .id(2L)
                    .title("Read a book")
                    .description(null)
                    .priority(Priority.MEDIUM)
                    .completed(false)
                    .createdAt(fixedTime)
                    .updatedAt(fixedTime)
                    .build();

            when(todoService.createTodo(any(CreateTodoRequest.class))).thenReturn(todoWithoutDesc);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(2L))
                    .andExpect(jsonPath("$.title").value("Read a book"))
                    .andExpect(jsonPath("$.description").value(nullValue()));
        }

        @Test
        @DisplayName("400 Bad Request - missing title (null)")
        void createTodo_nullTitle_returns400() throws Exception {
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle(null);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(todoService, never()).createTodo(any());
        }

        @Test
        @DisplayName("400 Bad Request - blank title (whitespace only)")
        void createTodo_blankTitle_returns400() throws Exception {
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("   ");

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(todoService, never()).createTodo(any());
        }

        @Test
        @DisplayName("400 Bad Request - empty title string")
        void createTodo_emptyTitle_returns400() throws Exception {
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("");

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(todoService, never()).createTodo(any());
        }

        @Test
        @DisplayName("415 Unsupported Media Type - request without Content-Type")
        void createTodo_missingContentType_returns415() throws Exception {
            mockMvc.perform(post("/api/todos")
                            .content("{\"title\":\"Test\"}"))
                    .andExpect(status().isUnsupportedMediaType());

            verify(todoService, never()).createTodo(any());
        }

        @Test
        @DisplayName("400 Bad Request - empty request body")
        void createTodo_emptyBody_returns400() throws Exception {
            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(todoService, never()).createTodo(any());
        }

        @Test
        @DisplayName("500 Internal Server Error - service throws unexpected exception")
        void createTodo_serviceThrowsException_returns500() throws Exception {
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("Valid title");

            when(todoService.createTodo(any(CreateTodoRequest.class)))
                    .thenThrow(new RuntimeException("Unexpected DB error"));

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/todos - Get All Todos")
    class GetAllTodos {

        @Test
        @DisplayName("200 OK - returns list of todos")
        void getAllTodos_todosExist_returns200WithList() throws Exception {
            Todo second = Todo.builder()
                    .id(2L)
                    .title("Go for a run")
                    .description("30 minutes in the park")
                    .priority(Priority.LOW)
                    .completed(true)
                    .createdAt(fixedTime)
                    .updatedAt(fixedTime)
                    .build();

            when(todoService.getAllTodos()).thenReturn(List.of(sampleTodo, second));

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].title").value("Buy groceries"))
                    .andExpect(jsonPath("$[0].priority").value("HIGH"))
                    .andExpect(jsonPath("$[0].completed").value(false))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].title").value("Go for a run"))
                    .andExpect(jsonPath("$[1].priority").value("LOW"))
                    .andExpect(jsonPath("$[1].completed").value(true));

            verify(todoService, times(1)).getAllTodos();
        }

        @Test
        @DisplayName("200 OK - returns empty list when no todos exist")
        void getAllTodos_noTodos_returns200WithEmptyList() throws Exception {
            when(todoService.getAllTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(todoService, times(1)).getAllTodos();
        }

        @Test
        @DisplayName("200 OK - returns single todo in list")
        void getAllTodos_singleTodo_returns200WithOneItem() throws Exception {
            when(todoService.getAllTodos()).thenReturn(List.of(sampleTodo));

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].title").value("Buy groceries"));
        }

        @Test
        @DisplayName("200 OK - response contains expected JSON fields")
        void getAllTodos_verifyResponseFields() throws Exception {
            when(todoService.getAllTodos()).thenReturn(List.of(sampleTodo));

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].title").exists())
                    .andExpect(jsonPath("$[0].priority").exists())
                    .andExpect(jsonPath("$[0].completed").exists())
                    .andExpect(jsonPath("$[0].createdAt").exists())
                    .andExpect(jsonPath("$[0].updatedAt").exists());
        }

        @Test
        @DisplayName("200 OK - both completed and pending todos are returned")
        void getAllTodos_mixedCompletionStatus_returnsAll() throws Exception {
            List<Todo> mixed = List.of(
                    Todo.builder().id(1L).title("Pending task").priority(Priority.HIGH).completed(false)
                            .createdAt(fixedTime).updatedAt(fixedTime).build(),
                    Todo.builder().id(2L).title("Done task").priority(Priority.LOW).completed(true)
                            .createdAt(fixedTime).updatedAt(fixedTime).build()
            );

            when(todoService.getAllTodos()).thenReturn(mixed);

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.completed == false)]", hasSize(1)))
                    .andExpect(jsonPath("$[?(@.completed == true)]", hasSize(1)))
                    .andExpect(jsonPath("$[?(@.priority == 'HIGH')]", hasSize(1)))
                    .andExpect(jsonPath("$[?(@.priority == 'LOW')]", hasSize(1)));
        }

        @Test
        @DisplayName("500 Internal Server Error - service throws unexpected exception")
        void getAllTodos_serviceThrowsException_returns500() throws Exception {
            when(todoService.getAllTodos()).thenThrow(new RuntimeException("DB connection lost"));

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isInternalServerError());
        }
    }
}
