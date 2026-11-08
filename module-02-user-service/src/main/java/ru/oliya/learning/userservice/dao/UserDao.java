package ru.oliya.learning.userservice.dao;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.oliya.learning.userservice.entity.User;
import ru.oliya.learning.userservice.util.HibernateUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {
    private static final Logger logger =
            LoggerFactory.getLogger(UserDao.class);
    private final SessionFactory sessionFactory;
    public UserDao() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void save(User user) {
        logger.info("Начало создания пользователя");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                logger.info("Пользователь успешно сохранён. ID: {}", user.getId());
            } catch (Exception e) {
                logger.error("Ошибка при сохранении пользователя", e);
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException(
                        "Ошибка при сохранении пользователя", e
                );
            }
        }
    }

    public User findById(Long id) {
        logger.info("Поиск пользователя. ID: {}", id);
        try (Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, id);
            if (user == null) {
                logger.warn("Пользователь не найден. ID: {}", id);
            } else {
                logger.info("Пользователь найден. ID: {}", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя. ID: {}", id, e);
            throw new RuntimeException(
                    "Ошибка при поиске пользователя с id = " + id, e
            );
        }
    }

    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<User> users = session.createQuery(
                    "FROM User", User.class
            ).getResultList();
            logger.info("Получен список пользователей. Количество: {}", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей", e);
            throw new RuntimeException(
                    "Ошибка при получении списка пользователей", e
            );
        }
    }

    public void update(User user) {
        logger.info("Начало обновления пользователя. ID: {}", user.getId());
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User existingUser = session.find(User.class, user.getId());
                if (existingUser == null) {
                    throw new IllegalArgumentException(
                            "Пользователь с id = " + user.getId() + " не найден"
                    );
                }
                existingUser.setName(user.getName());
                existingUser.setEmail(user.getEmail());
                existingUser.setAge(user.getAge());
                transaction.commit();
                logger.info("Пользователь успешно обновлён. ID: {}", user.getId());
            } catch (Exception e) {
                logger.error("Ошибка при обновлении пользователя. ID: {}", user.getId(), e);
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException(
                        "Ошибка при обновлении пользователя", e
                );
            }
        }
    }

    public void deleteById(Long id) {
        logger.info("Начало удаления пользователя. ID: {}", id);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.find(User.class, id);
                if (user == null) {
                    throw new IllegalArgumentException(
                            "Пользователь с id = " + id + " не найден"
                    );
                }
                session.remove(user);
                transaction.commit();
                logger.info("Пользователь успешно удалён. ID: {}", id);
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                if (e instanceof IllegalArgumentException) {
                    logger.warn("Пользователь не найден. ID: {}", id);
                } else {
                    logger.error("Ошибка при удалении пользователя. ID: {}", id, e);
                }
                throw e;
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Ошибка при удалении пользователя с id = " + id, e
            );
        }
    }
}