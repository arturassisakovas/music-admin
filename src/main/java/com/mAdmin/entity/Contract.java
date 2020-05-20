package com.mAdmin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.mAdmin.enumerator.ContractType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "contracts")
public class Contract {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "number")
    private String number;

    
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractType type;

    
    @Column(name = "document_path")
    private String documentPath;

    
    @Column(name = "date_signed")
    private LocalDateTime dateSigned;

    
    @Column(name = "is_signed")
    private Boolean isSigned;

    
    @Column(name = "valid_from")
    private LocalDate validFrom;

    
    @Column(name = "valid_to")
    private LocalDate validTo;

    
    @Column(name = "termination_date")
    private LocalDate terminationDate;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "attendee_id")
    private Attendee attendee;

    
    @OneToMany(mappedBy = "contract")
    private Set<InvoiceRecord> invoiceRecords;

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public ContractType getType() {
        return type;
    }

    
    public void setType(ContractType type) {
        this.type = type;
    }

    
    public LocalDateTime getDateSigned() {
        return this.dateSigned;
    }

    
    public void setDateSigned(LocalDateTime dateSigned) {
        this.dateSigned = dateSigned;
    }

    
    public Attendee getAttendee() {
        return attendee;
    }

    
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }


    
    public LocalDate getValidFrom() {
        return validFrom;
    }

    
    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    
    public LocalDate getValidTo() {
        return validTo;
    }

    
    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Contract) && (getId() != null)) {
            return getId().equals(((Contract) other).getId());
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

    
    public String getNumber() {
        return number;
    }

    
    public void setNumber(String number) {
        this.number = number;
    }

    
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    
    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    
    public String getDocumentPath() {
        return documentPath;
    }

    
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    
    public Boolean isSigned() {
        return isSigned;
    }

    
    public void setSigned(Boolean signed) {
        isSigned = signed;
    }

    
    public Set<InvoiceRecord> getInvoiceRecords() {
        return invoiceRecords;
    }

    
    public void setInvoiceRecords(Set<InvoiceRecord> invoiceRecords) {
        this.invoiceRecords = invoiceRecords;
    }

    
    public Contract() {
    }
}
