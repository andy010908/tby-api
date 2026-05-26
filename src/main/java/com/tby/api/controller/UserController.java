package com.tby.api.controller;

import com.tby.api.db.User;
import com.tby.api.dto.ApiResponse;
import com.tby.api.dto.CreateUserRequest;
import com.tby.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.created(userService.createUser(request));
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getUser(@PathVariable Long userId) {
        return ApiResponse.ok(userService.getUserById(userId));
    }

    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
