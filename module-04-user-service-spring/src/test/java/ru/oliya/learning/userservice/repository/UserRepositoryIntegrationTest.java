package ru.oliya.learning.userservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import ru.oliya.learning.userservice.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void saveUser_shouldPersistUser() {
        User user = new User("Test User", "test@example.com", 25);
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        assertEquals("Test User", savedUser.getName());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals(25, savedUser.getAge());
    }

    @Test
    void findById_shouldReturnUser() {
        User user = new User("Test User", "find@example.com", 30);
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
        assertEquals("find@example.com", foundUser.get().getEmail());
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenEmailExists() {
        User user = new User("Test User", "exists@example.com", 25);
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("exists@example.com");
        assertTrue(exists);
    }
    @Test
    void existsByEmail_shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("unknown@example.com");
        assertFalse(exists);
    }

    @Test
    void saveUser_shouldThrowExceptionWhenEmailIsDuplicate() {
        User firstUser = new User("First User", "duplicate@example.com", 25);
        userRepository.saveAndFlush(firstUser);
        User secondUser = new User("Second User", "duplicate@example.com", 30);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(secondUser));
    }

}
