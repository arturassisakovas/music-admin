package com.mAdmin.service;

import java.util.List;
import java.util.Optional;

import com.mAdmin.entity.TrackPeriod;


public interface TrackPeriodService extends TimeSpanService<TrackPeriod> {

    
    List<TrackPeriod> getAll();

    
    Optional<TrackPeriod> getTrackPeriodById(Long id);

}
