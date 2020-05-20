package com.mAdmin.entity;

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

import com.mAdmin.enumerator.RecordReason;
import com.mAdmin.enumerator.RecordStatus;
import com.mAdmin.enumerator.RecordType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "records")

public class Record {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RecordType type;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private RecordReason reason;

    
    @Column(name = "description")
    private String description;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    
    @Column(name = "deadline")
    private LocalDateTime deadline;

    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    
    @ManyToOne
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    
    public Record() {

    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Record) && (getId() != null)) {
            return getId().equals(((Record) other).getId());
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return this.getClass().hashCode() + getId().hashCode();
        }
        return  super.hashCode();
    }



    
    public Long getId() {
        return id;
    }



    
    public void setId(Long id) {
        this.id = id;
    }



    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public Attendee getAttendee() {
        return attendee;
    }



    
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
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

    
    public RecordType getType() {
        return type;
    }

    
    public void setType(RecordType type) {
        this.type = type;
    }

    
    public RecordReason getReason() {
        return reason;
    }

    
    public void setReason(RecordReason reason) {
        this.reason = reason;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public RecordStatus getStatus() {
        return status;
    }

    
    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    
    public LocalDateTime getDeadline() {
        return deadline;
    }

    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
