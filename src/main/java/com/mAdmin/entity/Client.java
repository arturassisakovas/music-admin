package com.mAdmin.entity;

import com.mAdmin.enumerator.ClientStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@Table(name = "clients")

public class Client extends User {

    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "client")
    private Set<Attendee> attendees = new HashSet<>();

    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "client")
    private Set<Invoice> invoices = new HashSet<>();

    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_agreements",
    joinColumns = {@JoinColumn(name = "client_id")},
    inverseJoinColumns = {@JoinColumn(name = "agreement_id")})
    private Collection<Agreement> agreements = new ArrayList<>();

    
    @Column(name = "birth_date")
    private LocalDate birthDate;

    
    @Column(name = "foreigner")
    private Boolean foreigner;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClientStatus status;

    
    @Column(name = "confirmation_token")
    private String confirmationToken;

    
    @Column(name = "confirmation_code")
    private String confirmationCode;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    
    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public Set<Attendee> getAttendees() {
        return this.attendees;
    }

    
    public void setAttendees(Set<Attendee> attendees) {
        this.attendees = attendees;
    }

    
    public Boolean getForeigner() {
        return this.foreigner;
    }

    
    public void setForeigner(Boolean foreigner) {
        this.foreigner = foreigner;
    }

    
    public ClientStatus getStatus() {
        return status;
    }

    
    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    
    public String getConfirmationToken() {
        return confirmationToken;
    }

    
    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    
    public String getConfirmationCode() {
        return confirmationCode;
    }

    
    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    
    public Set<Invoice> getInvoices() {
        return invoices;
    }

    
    public void setInvoices(Set<Invoice> invoices) {
        this.invoices = invoices;
    }

    
    public LocalDate getBirthDate() {
        return birthDate;
    }

    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    
    public Collection<Agreement> getAgreements() {
        return agreements;
    }

    
    public void setAgreements(Collection<Agreement> agreements) {
        this.agreements = agreements;
    }

    
    public Client() {
    }

    
    public Client(String name, String surname, String password, String email, String phoneNumber) {
        super();
        setName(name);
        setSurname(surname);
        setPassword(password);
        setEmail(email);
        setPhone(phoneNumber);
    }

}
