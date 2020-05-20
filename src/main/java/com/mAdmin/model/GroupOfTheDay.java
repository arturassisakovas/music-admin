package com.mAdmin.model;

import com.mAdmin.entity.Group;
import com.mAdmin.entity.TrackWorkingHour;


public class GroupOfTheDay {

    
    private Group group;

    
    private String lessonTime;

    
    private Integer workoutHour;

    
    private TrackWorkingHour trackWorkingHour;

    
    public Group getGroup() {
        return group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
    }

    
    public String getLessonTime() {
        return lessonTime;
    }

    
    public void setLessonTime(String lessonTime) {
        this.lessonTime = lessonTime;
    }

    
    public Integer getWorkoutHour() {
        return workoutHour;
    }

    
    public void setWorkoutHour(Integer workoutHour) {
        this.workoutHour = workoutHour;
    }

    
    public TrackWorkingHour getTrackWorkingHour() {
        return trackWorkingHour;
    }

    
    public void setTrackWorkingHour(TrackWorkingHour trackWorkingHour) {
        this.trackWorkingHour = trackWorkingHour;
    }

}
