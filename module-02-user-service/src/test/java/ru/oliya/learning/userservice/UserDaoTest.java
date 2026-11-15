package ru.oliya.learning.userservice;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.oliya.learning.userservice.dao.UserDao;
import ru.oliya.learning.userservice.entity.User;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoTest {
    @Container
    private static final PostgreSQLContainer POSTGRES = TestDatabase.POSTGRES;
    private static SessionFactory sessionFactory;
    private static UserDao userDao;

    @BeforeAll
    static void setUp() {
        sessionFactory = TestDatabase.buildSessionFactory();
        userDao = new UserDao(sessionFactory);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanUp() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {session.createNativeMutationQuery(
                    "TRUNCATE TABLE users RESTART IDENTITY CASCADE"
                ).executeUpdate();
                transaction.commit();
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

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
    }

    @Test
    void shouldFindUserById() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("Find Test", email, 30);
        userDao.save(user);
        User foundUser = userDao.findById(user.getId());
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
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
        user.setName("New Name");
        user.setAge(26);
        userDao.update(user);
        User updatedUser = userDao.findById(user.getId());
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
        userDao.deleteById(user.getId());
        User deletedUser = userDao.findById(user.getId());
        assertNull(deletedUser);
    }

    @Test
    void shouldFindAllUsers() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("List Test", email, 28);
        userDao.save(user);
        List<User> users = userDao.findAll();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertTrue(users.stream().anyMatch(found -> found.getId().equals(user.getId())));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        List<User> users = userDao.findAll();
        assertNotNull(users, "Метод должен возвращать список, а не null");
        assertTrue(users.isEmpty(), "Список должен быть пустым");
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userDao.deleteById(-1L)
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        User user = new User("Update Test", email, 30);
        userDao.save(user);
        userDao.deleteById(user.getId());
        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> userDao.update(user));
        assertInstanceOf(
                IllegalArgumentException.class, exception.getCause()
        );
    }
}
