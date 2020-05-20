package com.mAdmin.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;


public class HoursListElement {
    
    private Long id;

    
    private String daysAndHours;

    
    private int subscriptionMonths;

    
    private BigDecimal totalPrice;

    
    private BigDecimal savedMoney;

    
    private int workouts;

    
    private LocalDate firstDay;

    
    private LocalDate lastDay;

    
    private BigDecimal singleWorkoutPrice;

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getDaysAndHours() {
        return daysAndHours;
    }

    
    public void setDaysAndHours(String daysAndHours) {
        this.daysAndHours = daysAndHours;
    }

    
    public int getSubscriptionMonths() {
        return subscriptionMonths;
    }

    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    
    public BigDecimal getSavedMoney() {
        return savedMoney;
    }

    
    public void setSavedMoney(BigDecimal savedMoney) {
        this.savedMoney = savedMoney;
    }

    
    public int getWorkouts() {
        return workouts;
    }

    
    public void setWorkouts(int workouts) {
        this.workouts = workouts;
    }

    
    public LocalDate getFirstDay() {
        return firstDay;
    }

    
    public void setFirstDay(LocalDate firstDay) {
        this.firstDay = firstDay;
    }

    
    public LocalDate getLastDay() {
        return lastDay;
    }

    
    public void setLastDay(LocalDate lastDay) {
        this.lastDay = lastDay;
    }

    
    public void setSubscriptionMonths(int subscriptionMonths) {
        this.subscriptionMonths = subscriptionMonths;
    }

    
    public void setSingleWorkoutPrice(BigDecimal singleWorkoutPrice) {
        this.singleWorkoutPrice = singleWorkoutPrice;
    }

    
    public String getFormattedTotalPrice() {
        return formatNumbers(totalPrice);
    }

    
    public String getSingleWorkoutPrice() {
        return formatNumbers(singleWorkoutPrice);
    }

    
    public String getFormattedSavedMoney() {
        return formatNumbers(savedMoney);
    }

    
    public HoursListElement(Long id, String daysAndHours, BigDecimal totalPrice, BigDecimal savedMoney, int workouts,
            LocalDate firstDay, LocalDate lastDay, BigDecimal singleWorkoutPrice) {
        super();
        this.id = id;
        this.daysAndHours = daysAndHours;
        this.totalPrice = totalPrice;
        this.savedMoney = savedMoney;
        this.workouts = workouts;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.singleWorkoutPrice = singleWorkoutPrice;
    }

    
    public HoursListElement() {
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof HoursListElement) && (id != null) ? id.equals(((HoursListElement) other).id)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }

    public String formatNumbers(BigDecimal number) {
        DecimalFormatSymbols formatPrices = new DecimalFormatSymbols(Locale.getDefault());
        formatPrices.setDecimalSeparator(',');
        formatPrices.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("####,###0.00", formatPrices);

        return df.format(number);
    }

}
