package com.example.shopuser.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopuser.dto.UserDto;
import com.example.shopuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    public ApiResponse<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        return userService.createUser(dto);
    }

    @GetMapping
    public ApiResponse<List<UserDto>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }
}
