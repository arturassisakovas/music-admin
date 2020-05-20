package com.mAdmin.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;
import com.mAdmin.entity.PoolNonWorkday;
import com.mAdmin.entity.TrackWorkingHour;


public interface GroupWorkoutService {

    
    List<GroupWorkout> getAllGroupWorkouts(LocalDate startDate,
            LocalDate endDate,
            List<PoolNonWorkday> nonWorkdays,
            Set<TrackWorkingHour> trackWorkingHours,
            Group group);

    
    Map<Month, List<LocalDate>> getGroupWorkoutsDates(
            Long groupId,
            LocalDate startDate,
            LocalDate endDate,
            int subscriptionMonths);

    
    int getLessonCount(Attendee attendee, LocalDate endDate, LocalDate startDate);
}
