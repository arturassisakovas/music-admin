package com.mAdmin.view;

import java.util.ArrayList;
import java.util.List;


public class CoachTakenDaysModel {

    
    private int weekdayOrdinal;

    
    private String weekDayName;

    
    private List<CoachTakenTimeModel> coachTakenTimes = new ArrayList<>();

    
    public CoachTakenDaysModel(int weekdayOrdinal) {
        this.weekdayOrdinal = weekdayOrdinal;
    }

    
    public int getWeekdayOrdinal() {
        return weekdayOrdinal;
    }

    
    public void setWeekdayOrdinal(int weekdayOrdinal) {
        this.weekdayOrdinal = weekdayOrdinal;
    }

    
    public String getWeekDayName() {
        return weekDayName;
    }

    
    public void setWeekDayName(String weekDayName) {
        this.weekDayName = weekDayName;
    }

    
    public List<CoachTakenTimeModel> getCoachTakenTimes() {
        return coachTakenTimes;
    }

    
    public void setCoachTakenTimes(List<CoachTakenTimeModel> coachTakenTimes) {
        this.coachTakenTimes = coachTakenTimes;
    }
}
