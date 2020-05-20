package com.mAdmin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@MappedSuperclass
public class TimeSpan {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

    
    public Long getId() {
        return id;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
