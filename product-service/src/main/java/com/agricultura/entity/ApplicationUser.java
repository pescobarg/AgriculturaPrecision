package com.agricultura.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class ApplicationUser extends PanacheEntity {
    
    @Column(nullable = false, unique = true)
    public String username;
    
    @Column(nullable = false)
    public String email;
    
    @Column(name = "first_name", nullable = false)
    public String firstName;
    
    @Column(name = "last_name", nullable = false)
    public String lastName;
    
    public String telefono;
    
    @Column(nullable = false)
    public String role;
    
    public static ApplicationUser findByUsername(String username) {
        return find("username", username).firstResult();
    }
}