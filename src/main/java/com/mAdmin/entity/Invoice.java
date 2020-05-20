package com.mAdmin.entity;

import com.mAdmin.enumerator.InvoiceType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "invoices")
public class Invoice {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "invoice_number")
    private String invoiceNumber;

    
    @Column(name = "valid")
    private boolean valid = true;

    
    @Column(name = "invoice_type")
    @Enumerated(EnumType.STRING)
    private InvoiceType type;

    
    @Column(name = "document_path")
    private String documentPath;

    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "invoice")
    private List<InvoiceRecord> records = new ArrayList<>();

    
    @Column(name = "expire_date")
    private LocalDate expireDate;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice")
    private List<Subscription> subscriptions;

    
    @ManyToOne
    @JoinFormula("(select p.id from payments p where p.invoice_id = id order by p.id desc limit 1)")
    private Payment payment;

    
    public Long getId() {
        return this.id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    
    public boolean isValid() {
        return valid;
    }

    
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    
    public InvoiceType getType() {
        return type;
    }

    
    public void setType(InvoiceType type) {
        this.type = type;
    }

    
    public List<InvoiceRecord> getRecords() {
        return records;
    }

    
    public void setRecords(List<InvoiceRecord> records) {
        this.records = records;
    }

    
    public BigDecimal getPrice() {
        List<InvoiceRecord> invoiceRecords = this.getRecords();
        return invoiceRecords.stream().map(InvoiceRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    
    public LocalDate getExpireDate() {
        return this.expireDate;
    }

    
    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
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

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    
    public String getDocumentPath() {
        return documentPath;
    }

    
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    
    public Payment getPayment() {
        return payment;
    }

    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    
    public Invoice() {

    }

}
