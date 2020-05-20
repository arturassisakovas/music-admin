package com.mAdmin.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "discounts")
public class Discount {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "period_id")
    private Period period;

    
    @Column(name = "subscription_months")
    private int subscriptionMonths;

    
    @Column(name = "discount_rate")
    private int discountRate;

    
    public Discount(Period period, int subscriptionMonths, int discountRate) {
        this.period = period;
        this.subscriptionMonths = subscriptionMonths;
        this.discountRate = discountRate;
    }

    
    public Discount(int subscriptionMonths) {
        this.subscriptionMonths = subscriptionMonths;
    }

    
    public Discount() {

    }

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public Period getPeriod() {
        return period;
    }

    
    public void setPeriod(Period period) {
        this.period = period;
    }

    
    public int getSubscriptionMonths() {
        return subscriptionMonths;
    }

    
    public void setSubscriptionMonths(int subscriptionMonths) {
        this.subscriptionMonths = subscriptionMonths;
    }

    
    public int getDiscountRate() {
        return discountRate;
    }

    
    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

}
