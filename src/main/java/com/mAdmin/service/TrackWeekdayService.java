package com.mAdmin.service;

import java.util.List;
import java.util.Optional;

import com.mAdmin.entity.TrackWeekday;


public interface TrackWeekdayService {

    
    List<TrackWeekday> getAll();

    
    Optional<TrackWeekday> getTrackWeekdayById(Long id);

}
