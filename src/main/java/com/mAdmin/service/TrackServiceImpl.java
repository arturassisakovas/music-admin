package com.mAdmin.service;

import java.util.List;
import java.util.Optional;

import com.mAdmin.repository.CabinetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Track;


@Service
public class TrackServiceImpl implements TrackService{

    
    @Autowired
    private CabinetRepository cabinetRepository;

    
    public List<Track> getAll() {

        return cabinetRepository.findAll();

    }

    
    public Optional<Track> getTrackById(Long id) {

        return cabinetRepository.findById(id);

    }

}
