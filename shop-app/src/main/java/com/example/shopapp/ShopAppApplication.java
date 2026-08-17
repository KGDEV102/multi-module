package com.example.shopapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example")
@EntityScan(basePackages = "com.example.shopuser.entity")
@EnableJpaRepositories(basePackages = "com.example.shopuser.repository")
public class ShopAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopAppApplication.class, args);
    }

}
