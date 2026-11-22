package ru.oliya.learning.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oliya.learning.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}