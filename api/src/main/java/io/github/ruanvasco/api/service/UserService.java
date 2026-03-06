package io.github.ruanvasco.api.service;

import io.github.ruanvasco.api.dto.UserDto;
import io.github.ruanvasco.api.entity.User;
import io.github.ruanvasco.api.exception.FileProcessingException;
import io.github.ruanvasco.api.exception.UserNotFoundException;
import io.github.ruanvasco.api.mapper.UserMapper;
import io.github.ruanvasco.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final RestClient restClient;


    @Transactional
    public void processUsersFromUrl(String url) {
        try {
            byte[] fileBytes = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(byte[].class);

            if (fileBytes != null && fileBytes.length > 0) {
                try (InputStream inputStream = new java.io.ByteArrayInputStream(fileBytes)) {
                    saveInBatches(inputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileProcessingException("Error fetching or processing remote JSON", e);
        }
    }

    private void saveInBatches(InputStream inputStream) throws IOException {
        MappingIterator<UserDto> iterator = objectMapper.readerFor(UserDto.class).readValues(inputStream);
        List<User> batch = new ArrayList<>();
        int batchSize = 1000;

        while (iterator.hasNext()) {
            batch.add(userMapper.toEntity(iterator.next()));

            if (batch.size() >= batchSize) {
                userRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            userRepository.saveAll(batch);
        }
    }

    public UserDto findById(String id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
