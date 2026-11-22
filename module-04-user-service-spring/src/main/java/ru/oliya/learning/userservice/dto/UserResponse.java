package ru.oliya.learning.userservice.dto;

public record UserResponse(Long id, String name, String email, Integer age, java.time.LocalDateTime createdAt) {

}
