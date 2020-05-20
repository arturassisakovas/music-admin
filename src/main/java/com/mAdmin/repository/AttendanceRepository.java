package com.mAdmin.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mAdmin.entity.Attendance;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Group;


public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    
    List<Attendance> findByGroup(Group group);

    
    List<Attendance> findByGroupAndAttendee(Group group, Attendee attendee);

    
    List<Attendance> findByGroupAndWorkoutDateAndWorkoutHour(Group group, LocalDate workoutDate, int workoutHour);

    
    int countByGroupAndWorkoutDateAndWorkoutHour(Group group, LocalDate workoutDate, int workoutHour);

    
    List<Attendance> findFirst3ByAttendeeOrderByWorkoutDateDescWorkoutHourDesc(Attendee attendee);

    
    List<Attendance> findFirst5ByAttendeeOrderByWorkoutDateDescWorkoutHourDesc(Attendee attendee);

    
    List<Attendance> findFirst2ByAttendeeOrderByWorkoutDateDescWorkoutHourDesc(Attendee attendee);
    
    List<Attendance> getByAttendee(Attendee attendee);

    
    List<Attendance> findByAttendeeAndWorkoutDateAfterAndWorkoutDateBeforeAndIsPresentFalse(Attendee attendee,
            LocalDate validFrom, LocalDate validTo);

    
    @Query("select a from Attendance a where a.group.id = ?1 and a.workoutDate >= ?2 and a.workoutDate <= ?3")
    List<Attendance> findByGroupIdAndDateBetween(Long groupId, LocalDate startDate, LocalDate endDate);

    
    @Query("select a from Attendance a where a.attendee.id = ?1 and a.workoutDate >= ?2 and a.workoutDate <= ?3")
    List<Attendance> findByAttendeeAndDateBetween(Long attendeeId, LocalDate startDate, LocalDate endDate);

    
    int countByAttendeeAndIsPresentTrue(Attendee attendee);

    
    List<Attendance> findByAttendee(List<Attendee> attendees);
}
