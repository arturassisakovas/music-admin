package com.mAdmin.view;

import com.mAdmin.enumerator.DayOfWeek;
import com.mAdmin.util.TimeUtil;


public class CoachTakenTimeModel {

    
    private String coachName;

    
    private String coachTakenPool;

    
    private DayOfWeek dayOfWeek;

    
    private int workTimeStart;

    
    private int workTimeEnd;

    
    public CoachTakenTimeModel(String coachName, String coachTakenPool, DayOfWeek dayOfWeek,
                               int workTimeStart, int workTimeEnd) {
        this.coachName = coachName;
        this.coachTakenPool = coachTakenPool;
        this.dayOfWeek = dayOfWeek;
        this.workTimeStart = workTimeStart;
        this.workTimeEnd = workTimeEnd;
    }

    
    public String getFormattedWorkTime() {
        return getWorkTimeStart() + " - " + getWorkTimeEnd();
    }

    
    public String getCoachName() {
        return coachName;
    }

    
    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    
    public String getCoachTakenPool() {
        return coachTakenPool;
    }

    
    public void setCoachTakenPool(String coachTakenPool) {
        this.coachTakenPool = coachTakenPool;
    }

    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    
    public String getWorkTimeStart() {
        return TimeUtil.minutesToTimeConverter(workTimeStart);
    }

    
    public void setWorkTimeStart(int workTimeStart) {
        this.workTimeStart = workTimeStart;
    }

    
    public String getWorkTimeEnd() {
        return TimeUtil.minutesToTimeConverter(workTimeEnd);
    }

    
    public void setWorkTimeEnd(int workTimeEnd) {
        this.workTimeEnd = workTimeEnd;
    }
}
