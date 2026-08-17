package com.example.shopuser.repository;

import com.example.shopuser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
