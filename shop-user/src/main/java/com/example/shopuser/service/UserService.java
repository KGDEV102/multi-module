package com.example.shopuser.service;

import com.example.shopcore.exception.BusinessException;
import com.example.shopuser.dto.UserDto;
import com.example.shopuser.entity.User;
import com.example.shopuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.shopcore.dto.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ApiResponse<UserDto> createUser(UserDto userDto) {
        User existUser = userRepository.findByUsername(userDto.getUsername());
        if(existUser != null){
            throw new BusinessException("User already exists");
        }
        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .role(userDto.getRole()!=null?userDto.getRole():"USER")
                .build();
        userRepository.save(user);
        userDto.setId(user.getId());
        return new ApiResponse<UserDto>().success(userDto);
    }

    public ApiResponse<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());
        return new  ApiResponse<List<UserDto>>().success(users);
    }
    public ApiResponse<UserDto> getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User không tồn tại"));
        return new ApiResponse<UserDto>().success(
                UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build()
        );

    }
    public ApiResponse<Void> deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("User không tồn tại");
        }
        userRepository.deleteById(id);
        return new ApiResponse<Void>().success(null);
    }

}
