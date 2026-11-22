package ru.oliya.learning.userservice.dto;
import jakarta.validation.constraints.*;

public record UserCreateRequest(
        @NotBlank(message = "Имя обязательно для заполнения")
        String name,
        @NotBlank(message = "Email обязателен")
        @Email(message = "Укажите корректный email")
        String email,
        @NotNull(message = "Возраст обязателен")
        @Min(value = 18, message = "Возраст должен быть не меньше 18 лет")
        @Max(value = 120, message = "Возраст не может быть больше 120 лет")
        Integer age) {

}
