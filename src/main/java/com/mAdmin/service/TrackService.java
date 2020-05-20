package com.mAdmin.service;

import java.util.List;
import java.util.Optional;

import com.mAdmin.entity.Track;


public interface TrackService {

    
    List<Track> getAll();

    
    Optional<Track> getTrackById(Long id);

}
