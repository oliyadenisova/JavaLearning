package ru.oliya.learning.userservice;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.oliya.learning.userservice.entity.User;

public final class TestDatabase {
    public static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:18-alpine")
                    .withDatabaseName("user_service_test")
                    .withUsername("test")
                    .withPassword("test");
    private TestDatabase() {
    }

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.setProperty(
                "hibernate.connection.driver_class",
                "org.postgresql.Driver"
        );
        configuration.setProperty(
                "hibernate.connection.url",
                POSTGRES.getJdbcUrl()
        );
        configuration.setProperty(
                "hibernate.connection.username",
                POSTGRES.getUsername()
        );
        configuration.setProperty(
                "hibernate.connection.password",
                POSTGRES.getPassword()
        );
        configuration.setProperty(
                "hibernate.hbm2ddl.auto",
                "create-drop"
        );
        configuration.setProperty(
                "hibernate.show_sql",
                "true"
        );
        configuration.setProperty(
                "hibernate.format_sql",
                "true"
        );
        return configuration.buildSessionFactory();
    }
}