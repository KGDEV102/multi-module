package com.example.shopcore.enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @CreationTimestamp
            @Column(nullable = false, updatable = false)
    Instant createdAt;
    @UpdateTimestamp
    Instant updatedAt;
}
