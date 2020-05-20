package com.mAdmin.model;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Subscription;

import java.time.LocalDate;


public class InvoiceRecordCreationModel {

    
    private InvoiceRecord invoiceRecord;

    
    private Attendee attendee;

    
    private Contract contract;

    
    private Group group;

    
    private Long periodId;

    
    private Invoice invoice;

    
    private Subscription subscription;

    
    private LocalDate workoutStartDate;

    
    private LocalDate workoutEndDate;

    
    public InvoiceRecordCreationModel(InvoiceRecord invoiceRecord, Attendee attendee, Contract contract,
                                      Group group, Long periodId, Invoice invoice, Subscription subscription,
                                      LocalDate workoutStartDate, LocalDate workoutEndDate) {
        super();
        this.invoiceRecord = invoiceRecord;
        this.attendee = attendee;
        this.contract = contract;
        this.group = group;
        this.periodId = periodId;
        this.invoice = invoice;
        this.subscription = subscription;
        this.workoutStartDate = workoutStartDate;
        this.workoutEndDate = workoutEndDate;
    }

    
    public InvoiceRecord getInvoiceRecord() {
        return invoiceRecord;
    }

    
    public void setInvoiceRecord(InvoiceRecord invoiceRecord) {
        this.invoiceRecord = invoiceRecord;
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

    
    public Group getGroup() {
        return group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
    }

    
    public Long getPeriodId() {
        return periodId;
    }

    
    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    
    public Invoice getInvoice() {
        return invoice;
    }

    
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    
    public Subscription getSubscription() {
        return subscription;
    }

    
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    
    public LocalDate getWorkoutStartDate() {
        return workoutStartDate;
    }

    
    public void setWorkoutStartDate(LocalDate workoutStartDate) {
        this.workoutStartDate = workoutStartDate;
    }

    
    public LocalDate getWorkoutEndDate() {
        return workoutEndDate;
    }

    
    public void setWorkoutEndDate(LocalDate workoutEndDate) {
        this.workoutEndDate = workoutEndDate;
    }
}
