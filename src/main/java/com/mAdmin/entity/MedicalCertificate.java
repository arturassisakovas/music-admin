package com.mAdmin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mAdmin.enumerator.MedicalCertificateStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "medical_certificates")
public class MedicalCertificate {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    
    @JoinColumn(name = "name")
    private String name;

    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MedicalCertificateStatus status;

    
    @Column(name = "document_url")
    private String documentUrl;

    
    @Column(name = "valid_to")
    private LocalDate validTo;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public Attendee getAttendee() {
        return attendee;
    }

    
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    
    public MedicalCertificateStatus getStatus() {
        return status;
    }

    
    public void setStatus(MedicalCertificateStatus status) {
        this.status = status;
    }

    
    public String getDocumentUrl() {
        return documentUrl;
    }

    
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    
    public LocalDate getValidTo() {
        return validTo;
    }

    
    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
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
}
