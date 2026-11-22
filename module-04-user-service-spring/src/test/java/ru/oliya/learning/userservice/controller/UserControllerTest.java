package ru.oliya.learning.userservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.oliya.learning.userservice.dto.UserCreateRequest;
import ru.oliya.learning.userservice.dto.UserUpdateRequest;
import ru.oliya.learning.userservice.exception.EmailAlreadyExistsException;
import ru.oliya.learning.userservice.exception.UserNotFoundException;
import ru.oliya.learning.userservice.service.UserService;
import ru.oliya.learning.userservice.entity.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        // Arrange
        User user = new User("Test User", "test@example.com", 25);
        when(userService.getAllUsers()).thenReturn(List.of(user));
        // Act + Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].age").value(25));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        // Arrange
        UserCreateRequest request = new UserCreateRequest("Test User", "test@example.com", 25);
        User user = new User("Test User", "test@example.com", 25);
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(user);
        // Act + Assert
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Test User",
                                "email": "test@example.com",
                                "age": 25
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }
    @Test
    void createUser_shouldReturnBadRequestWhenEmailInvalid() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Test User",
                                "email": "invalid-email",
                                "age": 25
                            }
                            """))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any(UserCreateRequest.class));
    }
    @Test
    void createUser_shouldReturnConflictWhenEmailExists() throws Exception {
        // Arrange
        when(userService.createUser(any(UserCreateRequest.class))).thenThrow(new EmailAlreadyExistsException(
                        "Пользователь с таким email уже существует"));
        // Act + Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Test User",
                                "email": "test@example.com",
                                "age": 25
                            }
                            """))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        // Arrange
        User user = new User("Test User", "test@example.com", 25);
        when(userService.getUserById(1L)).thenReturn(user);
        // Act + Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }
    @Test
    void getUserById_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));
        // Act + Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        // Arrange
        User user = new User("Updated User", "updated@example.com", 30);
        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(user);
        // Act + Assert
        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Updated User",
                                "email": "updated@example.com",
                                "age": 30
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }
    @Test
    void updateUser_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Arrange
        when(userService.updateUser(eq(999L), any(UserUpdateRequest.class)))
                .thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));
        // Act + Assert
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Updated User",
                                "email": "updated@example.com",
                                "age": 30
                            }
                            """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);
        // Act + Assert
        mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());
        verify(userService).deleteUser(1L);
    }
    @Test
    void deleteUser_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Arrange
        doThrow(new UserNotFoundException("Пользователь с ID 999 не найден")).when(userService).deleteUser(999L);
        // Act + Assert
        mockMvc.perform(delete("/api/users/999")).andExpect(status().isNotFound());
        verify(userService).deleteUser(999L);
    }
}
