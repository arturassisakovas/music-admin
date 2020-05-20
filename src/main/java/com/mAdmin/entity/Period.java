package com.mAdmin.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mAdmin.enumerator.PeriodType;


@Entity
@Table(name = "periods")
public class Period extends TimeSpan {

    
    @Column(name = "name", nullable = false)
    private String name;

    
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    
    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;

    
    public Period(Season season, String name, PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        this.season = season;
        setName(name);
        this.periodType = periodType;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    
    public Period() {

    }

    
    public String getName() {
        return this.name;
    }

    
    public void setName(String name) {
        this.name = name.trim();
    }

    
    public PeriodType getPeriodType() {
        return periodType;
    }

    
    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    
    public Season getSeason() {
        return this.season;
    }

    
    public void setSeason(Season season) {
        this.season = season;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Period) && (getId() != null)) {
            return getId().equals(((Period) other).getId());
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return (this.getClass().hashCode() + getId().hashCode());
        }
        return super.hashCode();
    }

}
