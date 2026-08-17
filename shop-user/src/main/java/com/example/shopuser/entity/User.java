package com.example.shopuser.entity;

import com.example.shopcore.enity.BaseEntity;
import com.example.shopuser.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends BaseEntity {
    @Column(nullable=false, unique=true, length=50)
    String username;
    @Column(nullable=false)
    String password;
    @Column(nullable=false,unique=true)
    String email;
    @Column(nullable=false)
    String role;
}
