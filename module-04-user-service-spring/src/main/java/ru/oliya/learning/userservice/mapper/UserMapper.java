package ru.oliya.learning.userservice.mapper;

import ru.oliya.learning.userservice.dto.UserResponse;
import ru.oliya.learning.userservice.entity.User;

public class UserMapper {
    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getCreatedAt());
    }
}
