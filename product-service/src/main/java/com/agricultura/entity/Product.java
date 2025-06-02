package com.agricultura.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product extends PanacheEntity {
    
    @Column(nullable = false)
    public String name;
    
    public String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;
    
    @Column(nullable = false)
    public Integer stock;
    
    @Column(name = "owner_id", nullable = false)
    public Integer ownerId;
    
    @Column(name = "owner_username", nullable = false)
    public String ownerUsername;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}