package io.github.ruanvasco.api.service;

import io.github.ruanvasco.api.dto.UserDto;
import io.github.ruanvasco.api.entity.User;
import io.github.ruanvasco.api.mapper.UserMapper;
import io.github.ruanvasco.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Transactional
    public void processUsersFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            MappingIterator<UserDto> iterator = objectMapper.readerFor(UserDto.class).readValues(inputStream);
            List<User> userBatch = new ArrayList<>();
            int batchSize = 1000;

            while (iterator.hasNext()) {
                UserDto userDto = iterator.next();
                userBatch.add(userMapper.toEntity(userDto));

                if (userBatch.size() >= batchSize ) {
                    userRepository.saveAll(userBatch);
                    userBatch.clear();
                }
            }

            if (!userBatch.isEmpty()) {
                userRepository.saveAll(userBatch);
            }

        } catch (Exception e) {
            throw new RuntimeException(e); // Criar uma exceção personalizada
        }
    }

    public UserDto findById(String id) {
        User user = userRepository.findById(id).orElseThrow();
        return userMapper.toDto(user);
    }

}
