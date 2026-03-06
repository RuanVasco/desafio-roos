package io.github.ruanvasco.api.service;

import org.springframework.web.client.RestClient;
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

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ObjectReader objectReader;

    @Mock
    private MappingIterator<UserDto> mappingIterator;

    @InjectMocks
    private UserService userService;

    private void mockRestClientChain() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
    }

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
        String url = "http://invalid.com/data.json";
        mockRestClientChain();
        when(responseSpec.body(byte[].class)).thenThrow(new RuntimeException("Network error"));

        assertThrows(FileProcessingException.class, () -> userService.processUsersFromUrl(url));
    }

    @Test
    void processUsersFromUrlSuccess() throws Exception {
        String url = "https://raw.githubusercontent.com/Sementes-Roos/user-data-processing-api/refs/heads/main/mock-data.json";
        byte[] fakeData = "[]".getBytes();
        UserDto fakeUserDto = new UserDto("1", "John", "Doe", "john@test.com");
        User fakeUser = new User();

        mockRestClientChain();
        when(responseSpec.body(byte[].class)).thenReturn(fakeData);

        when(objectMapper.readerFor(UserDto.class)).thenReturn(objectReader);
        doReturn(mappingIterator).when(objectReader).readValues(any(InputStream.class));

        when(mappingIterator.hasNext()).thenReturn(true, false);
        when(mappingIterator.next()).thenReturn(fakeUserDto);
        when(userMapper.toEntity(fakeUserDto)).thenReturn(fakeUser);

        userService.processUsersFromUrl(url);

        verify(userRepository, times(1)).saveAll(anyList());
    }

}
