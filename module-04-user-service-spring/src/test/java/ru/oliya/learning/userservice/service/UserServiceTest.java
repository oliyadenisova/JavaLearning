package ru.oliya.learning.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.oliya.learning.userservice.dto.UserCreateRequest;
import ru.oliya.learning.userservice.entity.User;
import ru.oliya.learning.userservice.exception.EmailAlreadyExistsException;
import ru.oliya.learning.userservice.exception.UserNotFoundException;
import ru.oliya.learning.userservice.repository.UserRepository;
import ru.oliya.learning.userservice.dto.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnUsers() {
        // Arrange — подготовка
        User user = new User("Test User", "test@example.com", 25);
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        // Act — выполнение
        List<User> result = userService.getAllUsers();
        // Assert — проверка
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void createUser_shouldSaveUser() {
        // Arrange
        UserCreateRequest request =
                new UserCreateRequest("Test User", "test@example.com", 25);
        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        User result = userService.createUser(request);
        // Assert
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(25, result.getAge());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailExists() {
        // Arrange
        UserCreateRequest request =
                new UserCreateRequest("Test User", "test@example.com", 25);
        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);
        // Act + Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUser() {
        // Arrange
        User user = new User("Test User", "test@example.com", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Act
        User result = userService.getUserById(1L);
        // Assert
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_shouldUpdateUser() {
        // Arrange — подготовка
        User user = new User("Old Name", "old@test.com", 25);
        UserUpdateRequest request = new UserUpdateRequest("New Name", "new@test.com", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act — выполнение
        User result = userService.updateUser(1L, request);
        // Assert — проверка
        assertEquals("New Name", result.getName());
        assertEquals("new@test.com", result.getEmail());
        assertEquals(30, result.getAge());
        verify(userRepository).save(user);
    }
    @Test
    void updateUser_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest("New Name", "new@test.com", 30);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(999L, request));
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void updateUser_shouldThrowExceptionWhenEmailExists() {
        // Arrange
        User user = new User("Old Name", "old@test.com", 25);
        UserUpdateRequest request = new UserUpdateRequest("New Name", "taken@test.com", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);
        // Act + Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        // Arrange
        User user = new User("Test User", "test@example.com", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Act
        userService.deleteUser(1L);
        // Assert
        verify(userRepository).delete(user);
    }
    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository, never()).delete(any(User.class));
    }
}


