package com.agricultura.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem extends PanacheEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    public Cart cart;
    
    @Column(name = "product_id", nullable = false)
    public Long productId;
    
    @Column(name = "product_name", nullable = false)
    public String productName;
    
    @Column(name = "provider_id", nullable = false)
    public Integer providerId;
    
    @Column(name = "provider_username", nullable = false)
    public String providerUsername;
    
    @Column(nullable = false)
    public Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;
    
    @Column(name = "subtotal", precision = 10, scale = 2)
    public BigDecimal subtotal;
    
    @Column(name = "added_at", nullable = false)
    public LocalDateTime addedAt;
    
    @PrePersist
    public void prePersist() {
        addedAt = LocalDateTime.now();
        calculateSubtotal();
    }
    
    @PreUpdate
    public void preUpdate() {
        calculateSubtotal();
    }
    
    public void calculateSubtotal() {
        if (price != null && quantity != null) {
            subtotal = price.multiply(new BigDecimal(quantity));
        }
    }
    
    public static CartItem findByCartAndProduct(Cart cart, Long productId) {
        return find("cart = ?1 and productId = ?2", cart, productId).firstResult();
    }
}