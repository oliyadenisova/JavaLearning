package ru.oliya.learning.userservice.service;

import ru.oliya.learning.userservice.dao.UserDao;
import ru.oliya.learning.userservice.entity.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Общая валидация данных пользователя
    private void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Пользователь не может быть null"
            );
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Имя пользователя не может быть пустым"
            );
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Email не может быть пустым"
            );
        }
        if (!user.getEmail().matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            throw new IllegalArgumentException(
                    "Некорректный формат email"
            );
        }
        if (user.getAge() != null && user.getAge() < 0) {
            throw new IllegalArgumentException(
                    "Возраст не может быть отрицательным"
            );
        }
    }

    // Создание пользователя
    public void save(User user) {
        logger.info("Запрос на создание пользователя");
        try {
            validate(user);
            userDao.save(user);
            logger.info(
                    "Пользователь успешно создан. ID: {}",
                    user.getId()
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка валидации пользователя: {}", e.getMessage());
            throw e;
        }
    }

    // Поиск пользователя по ID
    public User findById(Long id) {
        logger.info("Запрос на поиск пользователя. ID: {}", id);
        if (id == null || id <= 0) {
            logger.warn("Передан некорректный ID: {}", id);
            throw new IllegalArgumentException(
                    "ID пользователя должен быть положительным числом"
            );
        }
        return userDao.findById(id);
    }

    // Получение всех пользователей
    public List<User> findAll() {
        logger.info("Запрос на получение списка пользователей");
        return userDao.findAll();
    }

    // Обновление пользователя
    public void update(User user) {
        logger.info("Запрос на обновление пользователя");
        try {
            validate(user);
            if (user.getId() == null || user.getId() <= 0) {
                throw new IllegalArgumentException(
                        "Для обновления необходим корректный ID пользователя"
                );
            }
            userDao.update(user);
            logger.info(
                    "Пользователь успешно обновлён. ID: {}",
                    user.getId()
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка валидации при обновлении: {}", e.getMessage());
            throw e;
        }
    }

    // Удаление пользователя
    public void deleteById(Long id) {
        logger.info("Запрос на удаление пользователя. ID: {}", id);
        if (id == null || id <= 0) {
            logger.warn("Передан некорректный ID для удаления: {}", id);
            throw new IllegalArgumentException(
                    "ID пользователя должен быть положительным числом"
            );
        }
        userDao.deleteById(id);
    }
}
