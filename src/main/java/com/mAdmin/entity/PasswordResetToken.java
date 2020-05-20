package com.mAdmin.entity;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;


@MappedSuperclass
public class PasswordResetToken {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    
    @Column(name = "expire_date", nullable = false)
    private Date expiryDate;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getToken() {
        return token;
    }

    
    public void setToken(String token) {
        this.token = token;
    }

    
    public Date getExpiryDate() {
        return expiryDate;
    }


    
    public void setExpiryDate(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.expiryDate = now.getTime();
    }

    
    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
