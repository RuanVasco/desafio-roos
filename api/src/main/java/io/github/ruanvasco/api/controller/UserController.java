package io.github.ruanvasco.api.controller;

import io.github.ruanvasco.api.dto.UserDto;
import io.github.ruanvasco.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadUsers(@RequestParam("file") MultipartFile file) {
        userService.processUsersFile(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }
}
