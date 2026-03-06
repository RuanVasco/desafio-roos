package io.github.ruanvasco.api.service;

import org.mockito.Answers;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import io.github.ruanvasco.api.dto.UserDto;
import io.github.ruanvasco.api.entity.User;
import io.github.ruanvasco.api.exception.FileProcessingException;
import io.github.ruanvasco.api.exception.UserNotFoundException;
import io.github.ruanvasco.api.mapper.UserMapper;
import io.github.ruanvasco.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @Test
    void findByIdReturnsUserDtoWhenUserExists() {
        String id = "5df38f6e695566a48211da8f";
        User user = new User(id, "Blankenship", "Vincent", "blankenshipvincent@rocklogic.com");
        UserDto userDto = new UserDto(id, "Blankenship", "Vincent", "blankenshipvincent@rocklogic.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Blankenship", result.firstName());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void findByIdThrowsUserNotFoundExceptionWhenUserDoesNotExist() {
        String id = "invalid_id";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void processUsersFromUrlThrowsFileProcessingExceptionOnError() {
        String url = "http://invalid-url.com/users.json";

        when(restClient.get()
                .uri(url)
                .retrieve()
                .body(InputStream.class))
                .thenThrow(new RuntimeException("Connection failed"));

        assertThrows(FileProcessingException.class, () -> userService.processUsersFromUrl(url));
    }


}
