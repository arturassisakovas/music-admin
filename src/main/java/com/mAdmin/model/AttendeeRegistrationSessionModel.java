package com.mAdmin.model;

import com.mAdmin.entity.Period;
import com.mAdmin.entity.Subscription;
import org.omnifaces.util.Faces;

import java.time.LocalDate;
import java.util.List;


public class AttendeeRegistrationSessionModel {

    
    private List<Subscription> subscriptions;

    
    private List<Period> periods;

    
    private List<LocalDate> firstDays;

    
    public AttendeeRegistrationSessionModel() {
        super();
        subscriptions = Faces.getSessionAttribute("theSubscription");
        periods = Faces.getSessionAttribute("periods");
        firstDays = Faces.getSessionAttribute("firstWorkoutDays");
    }

    
    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    
    public List<Period> getPeriods() {
        return periods;
    }

    
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    
    public List<LocalDate> getFirstDays() {
        return firstDays;
    }

    
    public void setFirstDays(List<LocalDate> firstDays) {
        this.firstDays = firstDays;
    }
}
