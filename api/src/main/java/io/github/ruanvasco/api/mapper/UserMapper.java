package io.github.ruanvasco.api.mapper;

import io.github.ruanvasco.api.dto.UserDto;
import io.github.ruanvasco.api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.id())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .build();
    }

}
