package com.mAdmin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "pools_non_workdays")
public class PoolNonWorkday {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "date")
    private LocalDate date;

    
    @ManyToOne
    @JoinColumn(name = "pool_id")
    private Pool pool;

    
    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    public Long getId() {
        return this.id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public LocalDate getDate() {
        return this.date;
    }

    
    public void setDate(LocalDate date) {
        this.date = date;
    }

    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public Pool getPool() {
        return pool;
    }

    
    public void setPool(Pool pool) {
        this.pool = pool;
    }

    
    public Season getSeason() {
        return season;
    }

    
    public void setSeason(Season season) {
        this.season = season;
    }

    
    public PoolNonWorkday() {
    }

    
    public PoolNonWorkday(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof PoolNonWorkday) && (id != null)) {
            return id.equals(((PoolNonWorkday) other).id);
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return (this.getClass().hashCode() + id.hashCode());
        }
        return super.hashCode();
    }
}
