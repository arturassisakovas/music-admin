package com.mAdmin.model;

import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class SubscriptionGenerationModel {

    
    private List<BigDecimal> listOfPrices;

    
    private List<LocalDate> listOfExpireDates;

    
    private List<Integer> listOfMonths;

    
    private List<Contract> listOfContracts;

    
    private List<Subscription> listOfSubscriptions;

    
    private List<LocalDate> listOfStartDates;

    
    public SubscriptionGenerationModel(List<BigDecimal> listOfPrices, List<LocalDate> listOfExpireDates,
                                       List<Integer> listOfMonths, List<Contract> listOfContracts,
                                       List<Subscription> listOfSubscriptions, List<LocalDate> listOfStartDates) {
        super();
        this.listOfPrices = listOfPrices;
        this.listOfExpireDates = listOfExpireDates;
        this.listOfMonths = listOfMonths;
        this.listOfContracts = listOfContracts;
        this.listOfSubscriptions = listOfSubscriptions;
        this.listOfStartDates = listOfStartDates;
    }

    
    public List<BigDecimal> getListOfPrices() {
        return listOfPrices;
    }

    
    public void setListOfPrices(List<BigDecimal> listOfPrices) {
        this.listOfPrices = listOfPrices;
    }

    
    public List<LocalDate> getListOfExpireDates() {
        return listOfExpireDates;
    }

    
    public void setListOfExpireDates(List<LocalDate> listOfExpireDates) {
        this.listOfExpireDates = listOfExpireDates;
    }

    
    public List<Integer> getListOfMonths() {
        return listOfMonths;
    }

    
    public void setListOfMonths(List<Integer> listOfMonths) {
        this.listOfMonths = listOfMonths;
    }

    
    public List<Contract> getListOfContracts() {
        return listOfContracts;
    }

    
    public void setListOfContracts(List<Contract> listOfContracts) {
        this.listOfContracts = listOfContracts;
    }

    
    public List<Subscription> getListOfSubscriptions() {
        return listOfSubscriptions;
    }

    
    public void setListOfSubscriptions(List<Subscription> listOfSubscriptions) {
        this.listOfSubscriptions = listOfSubscriptions;
    }

    
    public List<LocalDate> getListOfStartDates() {
        return listOfStartDates;
    }

    
    public void setListOfStartDates(List<LocalDate> listOfStartDates) {
        this.listOfStartDates = listOfStartDates;
    }
}
