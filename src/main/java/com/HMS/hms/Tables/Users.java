package com.HMS.hms.Tables;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Helper methods for role validation
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public boolean isHallManager() {
        return "HALL_MANAGER".equals(this.role);
    }

    public boolean isStudent() {
        return "STUDENT".equals(this.role);
    }

    public boolean isAuthority() {
        return "AUTHORITY".equals(this.role);
    }

    public boolean isSupervisor() {
        return "SUPERVISOR".equals(this.role);
    }
}
