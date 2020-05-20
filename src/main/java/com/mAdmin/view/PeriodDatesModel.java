package com.mAdmin.view;

import com.mAdmin.entity.Period;

import java.time.LocalDate;


public class PeriodDatesModel {

    
    private String name;

    
    private LocalDate startDate;

    
    private LocalDate endDate;

    
    private Period period;

    
    public PeriodDatesModel(String name, LocalDate startDate, LocalDate endDate, Period period) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.period = period;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    
    public Period getPeriod() {
        return period;
    }

    
    public void setPeriod(Period period) {
        this.period = period;
    }
}
