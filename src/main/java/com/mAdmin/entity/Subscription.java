package com.mAdmin.entity;

import com.mAdmin.enumerator.WorkoutsPerWeek;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "subscriptions")
public class Subscription extends TimeSpan {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    public Subscription() {
    }

    
    @Override
    public Long getId() {
        return id;
    }

    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name = "number_of_months", nullable = false)
    private int numberOfMonths;

    
    @Column(name = "workout_per_week")
    private WorkoutsPerWeek workoutsPerWeek;

    
    @Column(name = "price")
    private BigDecimal price;

    
    @ManyToOne(targetEntity = Attendee.class, fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    
    @ManyToOne(targetEntity = Invoice.class, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    
    public WorkoutsPerWeek getWorkoutsPerWeek() {
        return this.workoutsPerWeek;
    }

    
    public void setWorkoutsPerWeek(WorkoutsPerWeek workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    
    public int getNumberOfMonths() {
        return this.numberOfMonths;
    }

    
    public void setNumberOfMonths(int numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    
    public BigDecimal getPrice() {
        return price;
    }

    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    
    public Attendee getAttendee() {
        return attendee;
    }

    
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    
    public Invoice getInvoice() {
        return invoice;
    }

    
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    
    public Subscription(int numberOfMonths, LocalDate startDate, LocalDate endDate) {
        super();
        this.numberOfMonths = numberOfMonths;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    
    public Subscription(int numberOfMonths, BigDecimal price, LocalDate startDate, LocalDate endDate) {
        super();
        this.numberOfMonths = numberOfMonths;
        this.price = price;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Subscription) && (getId() != null)) {
            return getId().equals(((Subscription) other).getId());
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return this.getClass().hashCode() + getId().hashCode();
        }
        return super.hashCode();
    }

}
