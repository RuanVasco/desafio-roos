package io.github.ruanvasco.api.controller;

import io.github.ruanvasco.api.dto.UserDto;
import io.github.ruanvasco.api.dto.UserResponseDto;
import io.github.ruanvasco.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for user management and batch processing")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import and process users from a remote JSON URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import started successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid URL or processing error", content = @Content)
    })
    public ResponseEntity<Void> importUsers(@RequestParam("url") String url) {
        userService.processUsersFromUrl(url);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find user by ID", description = "Returns a single user wrapped in a response object.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(new UserResponseDto(user));
    }
}
