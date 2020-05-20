package com.mAdmin.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


@MappedSuperclass
public class User implements Comparable<User> {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "name", nullable = false)
    private String name;

    
    @Column(name = "surname", nullable = false)
    private String surname;

    
    @Column(name = "phone")
    private String phone;

    
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    
    @Column(name = "password", nullable = false)
    private String password;

    
    @Column(name = "enabled")
    private boolean enabled;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    public String getEmail() {
        return email;
    }

    
    public Long getId() {
        return id;
    }

    
    public String getPassword() {
        return password;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getSurname() {
        return surname;
    }

    
    public String getFullName() {
       return name + " " + surname;
    }

    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    
    public String getPhone() {
        return phone;
    }

    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    public Boolean getEnabled() {
        return enabled;
    }

    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public User() {
        super();
    }

    @Override
    public int compareTo(User  u) {
        if (getSurname() == null || u.getSurname() == null) {
            return 0;
        }
        return getSurname().compareTo(u.getSurname());
    }
}
