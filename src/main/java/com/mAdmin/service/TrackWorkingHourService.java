package com.mAdmin.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.mAdmin.entity.TrackWorkingHour;


public interface TrackWorkingHourService {

    
    List<TrackWorkingHour> getAll();

    
    Optional<TrackWorkingHour> getTrackWorkingHourById(Long id);

    
    List<LocalDate> getAllWorkoutDates(LocalDate startDate, LocalDate endDate, int weekDay);

    
    List<LocalDate> getWorkoutDatesWithoutNonWorkdays(List<LocalDate> workoutDays);
}
