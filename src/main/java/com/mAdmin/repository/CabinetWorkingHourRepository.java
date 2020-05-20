package com.mAdmin.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mAdmin.entity.Group;
import com.mAdmin.entity.TrackWeekday;
import com.mAdmin.entity.TrackWorkingHour;


public interface CabinetWorkingHourRepository extends JpaRepository<TrackWorkingHour, Long> {

    
    List<TrackWorkingHour> findByTrackWeekdayOrderByStartHourAsc(TrackWeekday trackWeekday);

    
    List<TrackWorkingHour> findByGroup(Group group);

    
    List<TrackWorkingHour> findByGroupAndTrackWeekday(Group group, TrackWeekday trackWeekday);

    
    List<TrackWorkingHour> findAllByGroupAndTrackWeekday(Group group, TrackWeekday trackWeekday);

    
    @Transactional
    @Modifying
    @Query("delete from TrackWorkingHour t where t.group = ?1")
    void deleteByGroup(Group group);

}
