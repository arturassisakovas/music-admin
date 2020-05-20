package com.mAdmin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "invoice_records")
public class InvoiceRecord {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    
    @ManyToOne
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    
    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    
    @Column(name = "gross_price")
    private BigDecimal grossPrice;

    
    @Column(name = "discount_rate")
    private Integer discountRate;

    
    @Column(name = "price")
    private BigDecimal price;

    
    @Column(name = "workouts_amount")
    private Integer workoutsAmount;

    
    @Column(name = "period_start_date")
    private LocalDate periodStartDate;

    
    @Column(name = "period_end_date")
    private LocalDate periodEndDate;

    
    public InvoiceRecord() {
        super();
    }

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public Invoice getInvoice() {
        return invoice;
    }

    
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    
    public Attendee getAttendee() {
        return attendee;
    }

    
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    
    public Contract getContract() {
        return contract;
    }

    
    public void setContract(Contract contract) {
        this.contract = contract;
    }

    
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    
    public void setGrossPrice(BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
    }

    
    public Integer getDiscountRate() {
        return discountRate;
    }

    
    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    
    public BigDecimal getPrice() {
        return price;
    }

    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    
    public Integer getWorkoutsAmount() {
        return workoutsAmount;
    }

    
    public void setWorkoutsAmount(Integer workoutsAmount) {
        this.workoutsAmount = workoutsAmount;
    }

    
    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    
    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    
    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    
    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    
    public Group getGroup() {
        return group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
    }

}
