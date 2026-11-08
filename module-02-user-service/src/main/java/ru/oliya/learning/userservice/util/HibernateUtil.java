package ru.oliya.learning.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY =
            buildSessionFactory();
    private HibernateUtil() {
    }
    private static SessionFactory buildSessionFactory() {
        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null || dbPassword.isBlank()) {
            throw new IllegalStateException(
                    "Не задана переменная окружения DB_PASSWORD"
            );
        }
        try {
            Configuration configuration = new Configuration()
                    .configure();
            configuration.setProperty(
                    "hibernate.connection.password",
                    dbPassword
            );
            return configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
