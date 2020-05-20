package com.mAdmin.service;

import com.mAdmin.entity.Attendance;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface AttendanceService {

    
    Optional<Attendance> getById(Long id);

    
    List<Attendance> getAll();

    
    List<Attendance> getByGroupAndAttendee(Group group, Attendee attendee);

    
    Map<String, Object> formUnmarkedAttendanceCoachDetails(Employee coach, Map<String,
            List<GroupWorkout>> unmarkedAttendances);

}
