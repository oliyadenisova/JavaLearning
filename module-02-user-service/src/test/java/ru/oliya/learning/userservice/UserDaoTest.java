package ru.oliya.learning.userservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.oliya.learning.userservice.dao.UserDao;
import ru.oliya.learning.userservice.entity.User;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {
    private final UserDao userDao = new UserDao();
    private Long createdUserId;

    @Test
    void shouldSaveUser() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("Test User", email, 25);
        userDao.save(user);
        assertNotNull(user.getId(), "После сохранения у пользователя должен появиться ID");
        User savedUser = userDao.findById(user.getId());
        assertNotNull(savedUser, "Пользователь должен находиться в базе");
        assertEquals("Test User", savedUser.getName());
        assertEquals(email, savedUser.getEmail());
        assertEquals(25, savedUser.getAge());
        createdUserId = savedUser.getId();
    }

    @AfterEach
    void cleanUp() {
        if (createdUserId != null) {
            userDao.deleteById(createdUserId);
        }
    }

    @Test
    void shouldFindUserById() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("Find Test", email, 30);
        userDao.save(user);
        createdUserId = user.getId();
        User foundUser = userDao.findById(createdUserId);
        assertNotNull(foundUser);
        assertEquals(createdUserId, foundUser.getId());
        assertEquals("Find Test", foundUser.getName());
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {
        User foundUser = userDao.findById(-1L);
        assertNull(foundUser);
    }

    @Test
    void shouldUpdateUser() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("Old Name", email, 25);
        userDao.save(user);
        createdUserId = user.getId();
        user.setName("New Name");
        user.setAge(26);
        userDao.update(user);
        User updatedUser = userDao.findById(createdUserId);
        assertNotNull(updatedUser);
        assertEquals("New Name", updatedUser.getName());
        assertEquals(26, updatedUser.getAge());
        assertEquals(email, updatedUser.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("Delete Test", email, 35);
        userDao.save(user);
        createdUserId = user.getId();
        userDao.deleteById(createdUserId);
        createdUserId = null;
        User deletedUser = userDao.findById(user.getId());
        assertNull(deletedUser);
    }

    @Test
    void shouldFindAllUsers() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("List Test", email, 28);
        userDao.save(user);
        createdUserId = user.getId();
        List<User> users = userDao.findAll();
        assertNotNull(users);
        assertTrue(users.stream()
                .anyMatch(found -> found.getId().equals(createdUserId)));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userDao.deleteById(-1L)
        );
    }
}
