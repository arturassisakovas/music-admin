package com.mAdmin.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "seasons")
public class Season extends TimeSpan {

    
    @Column(name = "name", nullable = false)
    private String name;

    
    @Column(name = "small_group_price", nullable = false)
    private BigDecimal smallGroupPrice;

    
    @Column(name = "big_group_price", nullable = false)
    private BigDecimal bigGroupPrice;

    
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "seasons")
    private List<Pool> pools = new ArrayList<>();


    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "season")
    private Set<Period> periods = new HashSet<>();

    
    public String getName() {
        return this.name;
    }

    
    public void setName(String name) {
        this.name = name.trim();
    }

    
    public BigDecimal getSmallGroupPrice() {
        return smallGroupPrice;
    }

    
    public void setSmallGroupPrice(BigDecimal smallGroupPrice) {
        this.smallGroupPrice = smallGroupPrice;
    }

    
    public BigDecimal getBigGroupPrice() {
        return bigGroupPrice;
    }

    
    public void setBigGroupPrice(BigDecimal bigGroupPrice) {
        this.bigGroupPrice = bigGroupPrice;
    }

    
    public List<Pool> getPools() {
        return pools;
    }

    
    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    
    public Season() {

    }

    
    public Season(String name, BigDecimal smallGroupPrice, BigDecimal bigGroupPrice,
                  LocalDate startDate, LocalDate endDate) {
        setName(name);
        this.smallGroupPrice = smallGroupPrice;
        this.bigGroupPrice = bigGroupPrice;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    
    public Set<Period> getPeriods() {
        return periods;
    }

    
    public void setPeriods(Set<Period> periods) {
        this.periods = periods;
    }
}
