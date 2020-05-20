package com.mAdmin.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "payments")
public class Payment {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "invoice_id")
    private Invoice invoice;

    
    @Column(name = "status")
    private Boolean status;

    
    @Column(name = "date_paid")
    private LocalDateTime datePaid;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    public Long getId() {
        return id;
    }

    
    public Invoice getInvoice() {
        return invoice;
    }

    
    public Boolean getStatus() {
        return status;
    }

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    
    public void setStatus(Boolean status) {
        this.status = status;
    }

    
    public LocalDateTime getDatePaid() {
        return datePaid;
    }

    
    public void setDatePaid(LocalDateTime datePaid) {
        this.datePaid = datePaid;
    }

}
