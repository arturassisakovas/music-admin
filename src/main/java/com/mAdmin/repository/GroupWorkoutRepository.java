package com.mAdmin.repository;

import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface GroupWorkoutRepository extends JpaRepository<GroupWorkout, Long> {

    
    List<GroupWorkout> findByGroupIdAndDateBetween(Long groupId, LocalDate startDate, LocalDate endDate);

    
    @Query("select gw from GroupWorkout gw"
            + " where gw.date >= :startDate and gw.date <= :endDate and gw.group.id = :groupId and gw.date not in"
            + " (select a.workoutDate from Attendance a"
            + " where a.group.id = :groupId and a.workoutDate >= :startDate and a.workoutDate <= :endDate)")
    List<GroupWorkout> findByGroupIdAndDateBetweenAndNotPresentInAttendance(@Param ("groupId") Long groupId,
                                                                            @Param("startDate") LocalDate startDate,
                                                                            @Param ("endDate") LocalDate endDate);
    
    int countByGroupIdAndDateBetween(Long groupId, LocalDate startDate, LocalDate endDate);

    
    @Query("select gw from GroupWorkout gw WHERE gw.date<=:yesterday and gw.date>=:firstWorkoutDate and "
            + "gw.group in (select g from" + " Group g JOIN g.employees c WHERE c.id = :coachId)")
    List<GroupWorkout> findOlderWorkoutDatesByCoachId(@Param("firstWorkoutDate") LocalDate firstWorkoutDate,
            @Param("coachId") Long id, @Param("yesterday") LocalDate yesterday);

    
    @Query("select gw from GroupWorkout gw where gw.group = ?1")
    List<GroupWorkout> findGroupWorkoutsByGroupId(Group group);

    
    GroupWorkout findFirstByGroupId(Long groupId);
    
    List<GroupWorkout> findByGroup(Group group);

}
