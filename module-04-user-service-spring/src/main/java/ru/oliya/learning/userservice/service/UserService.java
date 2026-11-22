package ru.oliya.learning.userservice.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.oliya.learning.userservice.dto.UserCreateRequest;
import ru.oliya.learning.userservice.dto.UserUpdateRequest;
import ru.oliya.learning.userservice.entity.User;
import ru.oliya.learning.userservice.exception.EmailAlreadyExistsException;
import ru.oliya.learning.userservice.exception.UserNotFoundException;
import ru.oliya.learning.userservice.repository.UserRepository;

@Service
public class UserService {
    private static final Logger log =
            LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        log.info("Запрос на получение списка пользователей");
        return userRepository.findAll();
    }

    public User createUser(UserCreateRequest request) {
        log.info("Попытка создать пользователя с email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Не удалось создать пользователя: email {} уже существует", request.email());
            throw new EmailAlreadyExistsException("Пользователь с таким email уже существует");}

        User user = new User(request.name(), request.email(), request.age());
        User savedUser = userRepository.save(user);
        log.info("Пользователь успешно создан. ID: {}", savedUser.getId());

        return savedUser;
    }

    public User getUserById(Long id) {
        log.info("Запрос на получение пользователя с ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new UserNotFoundException(
                            "Пользователь с ID " + id + " не найден");
                });
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        log.info("Запрос на обновление пользователя с ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("Не удалось обновить пользователя: ID {} не найден", id);
           return new UserNotFoundException(
                    "Пользователь с ID " + id + " не найден");
        });
        if (userRepository.existsByEmail(request.email())
                && !user.getEmail().equals(request.email())) {
            log.warn("Не удалось обновить пользователя {}: email {} уже занят", id, request.email());
            throw new EmailAlreadyExistsException(
                    "Пользователь с таким email уже существует");
        }
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());

        User updatedUser = userRepository.save(user);
        log.info("Пользователь с ID {} успешно обновлён", id);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        log.info("Запрос на удаление пользователя с ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("Не удалось удалить пользователя: ID {} не найден", id);
            return new UserNotFoundException(
                    "Пользователь с ID " + id + " не найден");
        });
        userRepository.delete(user);
        log.info("Пользователь с ID {} успешно удалён", id);
    }
}