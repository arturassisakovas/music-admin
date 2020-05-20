package com.mAdmin.model;

import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Subscription;
import com.mAdmin.enumerator.WorkoutsPerWeek;

import java.math.BigDecimal;
import java.time.LocalDate;


public class SubscriptionCreationModel {

    
    private Subscription subscription;

    
    private Contract contract;

    
    private Invoice invoice;

    
    private BigDecimal price;

    
    private WorkoutsPerWeek workoutsPerWeek;

    
    private LocalDate expireDate;

    
    private Integer months;

    
    private LocalDate startDate;

    
    public SubscriptionCreationModel(Subscription subscription, Contract contract, Invoice invoice, BigDecimal price,
                                     WorkoutsPerWeek workoutsPerWeek, LocalDate expireDate, Integer months,
                                     LocalDate startDate) {
        super();
        this.subscription = subscription;
        this.contract = contract;
        this.invoice = invoice;
        this.price = price;
        this.workoutsPerWeek = workoutsPerWeek;
        this.expireDate = expireDate;
        this.months = months;
        this.startDate = startDate;
    }

    public SubscriptionCreationModel(Subscription subscription, Invoice invoice, WorkoutsPerWeek workoutsPerWeek) {
        super();
        this.subscription = subscription;
        this.invoice = invoice;
        this.workoutsPerWeek = workoutsPerWeek;
    };

    
    public Subscription getSubscription() {
        return subscription;
    }

    
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    
    public Contract getContract() {
        return contract;
    }

    
    public void setContract(Contract contract) {
        this.contract = contract;
    }

    
    public Invoice getInvoice() {
        return invoice;
    }

    
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    
    public BigDecimal getPrice() {
        return price;
    }

    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    
    public WorkoutsPerWeek getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    
    public void setWorkoutsPerWeek(WorkoutsPerWeek workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    
    public LocalDate getExpireDate() {
        return expireDate;
    }

    
    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    
    public Integer getMonths() {
        return months;
    }

    
    public void setMonths(Integer months) {
        this.months = months;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
