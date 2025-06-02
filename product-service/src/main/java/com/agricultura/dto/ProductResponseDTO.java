package com.agricultura.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponseDTO {
    
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public Integer stock;
    public Integer ownerId;
    public String ownerUsername;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    
    public ProductResponseDTO() {}
    
    public ProductResponseDTO(Long id, String name, String description, BigDecimal price, 
                             Integer stock, Integer ownerId, String ownerUsername, 
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}