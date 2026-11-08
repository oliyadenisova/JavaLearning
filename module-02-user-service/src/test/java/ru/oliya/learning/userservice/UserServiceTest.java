package ru.oliya.learning.userservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.oliya.learning.userservice.dao.UserDao;
import ru.oliya.learning.userservice.entity.User;
import ru.oliya.learning.userservice.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveValidUser() {
        User user = new User(
                "Test User",
                "test@example.com",
                25
        );
        userService.save(user);
        verify(userDao, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.save(null)
        );
        verifyNoInteractions(userDao);
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        User user = new User("", "test@example.com", 25);
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.save(user)
        );
        verifyNoInteractions(userDao);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        User user = new User("Test User", "", 25);
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.save(user)
        );
        verifyNoInteractions(userDao);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        User user = new User("Test User", "invalid-email", 25);
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.save(user)
        );
        verifyNoInteractions(userDao);
    }

    @Test
    void shouldThrowExceptionWhenAgeIsNegative() {
        User user = new User("Test User", "test@example.com", -1);
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.save(user)
        );
        verifyNoInteractions(userDao);
    }

    @Test
    void shouldFindUserById() {
        User user = new User("Test User", "test@example.com", 25);
        when(userDao.findById(1L)).thenReturn(user);
        User foundUser = userService.findById(1L);
        assertNotNull(foundUser);
        assertEquals("Test User", foundUser.getName());
        verify(userDao).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenFindingUserWithInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.findById(-1L)
        );
        verifyNoInteractions(userDao);
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = List.of(
                new User("User One", "one@example.com", 25),
                new User("User Two", "two@example.com", 30)
        );
        when(userDao.findAll()).thenReturn(users);
        List<User> result = userService.findAll();
        assertEquals(2, result.size());
        verify(userDao).findAll();
    }

    @Test
    void shouldUpdateValidUser() {
        User user = new User("Test User", "test@example.com", 25);
        user.setId(14L);
        userService.update(user);
        verify(userDao).update(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUserWithoutId() {
        User user = new User("Test User", "test@example.com", 25);
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(user)
        );
        verifyNoInteractions(userDao);
    }

    @Test
    void shouldDeleteUserById() {
        userService.deleteById(1L);
        verify(userDao).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingUserWithInvalidId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteById(0L)
        );
        verifyNoInteractions(userDao);
    }
}
