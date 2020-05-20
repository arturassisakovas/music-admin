package com.mAdmin.model;

import com.mAdmin.enumerator.SwimmingLevel;

import java.time.LocalDate;


public class AttendeeCreationModel {

    
    private LocalDate birthDate;

    
    private LocalDate firstDay;

    
    private String city;

    
    private Long groupId;

    
    private SwimmingLevel swimLevel;

    
    public AttendeeCreationModel(
            LocalDate birthDate, LocalDate firstDay, String city, Long groupId, SwimmingLevel swimLevel) {
        this.birthDate = birthDate;
        this.firstDay = firstDay;
        this.city = city;
        this.groupId = groupId;
        this.swimLevel = swimLevel;
    }

    
    public LocalDate getBirthDate() {
        return birthDate;
    }

    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    
    public LocalDate getFirstDay() {
        return firstDay;
    }

    
    public void setFirstDay(LocalDate firstDay) {
        this.firstDay = firstDay;
    }

    
    public String getCity() {
        return city;
    }

    
    public void setCity(String city) {
        this.city = city;
    }

    
    public Long getGroupId() {
        return groupId;
    }

    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    
    public SwimmingLevel getSwimLevel() {
        return swimLevel;
    }

    
    public void setSwimLevel(SwimmingLevel swimLevel) {
        this.swimLevel = swimLevel;
    }
}
