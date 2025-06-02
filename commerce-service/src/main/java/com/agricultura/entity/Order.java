package com.agricultura.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntity {
    
    @Column(name = "order_number", nullable = false, unique = true)
    public String orderNumber;
    
    @Column(name = "buyer_id", nullable = false)
    public Integer buyerId;
    
    @Column(name = "buyer_username", nullable = false)
    public String buyerUsername;
    
    @Column(name = "seller_id", nullable = false)
    public Integer sellerId;
    
    @Column(name = "seller_username", nullable = false)
    public String sellerUsername;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    public BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "shipping_address")
    public String shippingAddress;
    
    @Column(name = "payment_method")
    public String paymentMethod;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    
    @Column(name = "shipped_at")
    public LocalDateTime shippedAt;
    
    @Column(name = "delivered_at")
    public LocalDateTime deliveredAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<OrderItem> items;
    
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
    
    public static List<Order> findByBuyerId(Integer buyerId) {
        return list("buyerId", buyerId);
    }
    
    public static List<Order> findBySellerId(Integer sellerId) {
        return list("sellerId", sellerId);
    }
    
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}