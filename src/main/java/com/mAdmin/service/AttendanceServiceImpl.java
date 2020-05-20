package com.mAdmin.service;

import com.mAdmin.repository.AttendanceRepository;
import com.mAdmin.entity.Attendance;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    
    @Autowired
    private AttendanceRepository attendanceRepository;

    
    @Override
    public Optional<Attendance> getById(Long id) {

        return attendanceRepository.findById(id);
    }

    
    @Override
    public List<Attendance> getAll() {

        return attendanceRepository.findAll();
    }

	
    @Override
    public List<Attendance> getByGroupAndAttendee(Group group, Attendee attendee) {
    	return attendanceRepository.findByGroupAndAttendee(group, attendee);
    }

    
    @Override
    public Map<String, Object> formUnmarkedAttendanceCoachDetails(
            Employee coach, Map<String, List<GroupWorkout>> unmarkedAttendances) {

            Map<String, Object> unmarkedCoachDetails = new HashMap<>();
            int unmarkedWorkoutsCount = 0;

            for (List<GroupWorkout> groupWorkouts : unmarkedAttendances.values()) {
                unmarkedWorkoutsCount += groupWorkouts.size();
            }

            if (unmarkedWorkoutsCount != 0) {
                unmarkedCoachDetails.put("coachName", coach.getName());
                unmarkedCoachDetails.put("coachSurname", coach.getSurname());
                unmarkedCoachDetails.put("coachEmail", coach.getEmail());
                unmarkedCoachDetails.put("coachPhoneNumber", coach.getPhone());
                unmarkedCoachDetails.put("unmarkedWorkoutsCount", unmarkedWorkoutsCount);
                unmarkedCoachDetails.put("unmarkedWorkouts", unmarkedAttendances);
            }
        return unmarkedCoachDetails;
    }
}
