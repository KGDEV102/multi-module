package com.example.shopuser.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    String id;
    @NotBlank(message = "Username không được để trống")
    String username;
    @NotBlank(message = "Password không được để trống")
    String password;
    @Email(message = "Email không hợp lệ")
    String email;
    String role;
}
