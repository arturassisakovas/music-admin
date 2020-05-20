package com.mAdmin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mAdmin.entity.TrackWeekday;
import com.mAdmin.enumerator.DayOfWeek;


public interface CabinetWeekdayRepository extends JpaRepository<TrackWeekday, Long> {

    
    List<TrackWeekday> findByTrackPeriodId(Long id);

    
    void deleteByDayOfWeekAndTrackPeriodId(DayOfWeek dayOfWeek, Long id);

    
    TrackWeekday findByDayOfWeekAndTrackPeriodId(DayOfWeek dayOfWeek, Long id);

    
    @Query("select twd from TrackWeekday twd JOIN twd.trackPeriod tp WHERE tp.id=?1")
    List<TrackWeekday> findByTrackId(Long id);
}
