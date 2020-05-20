package com.mAdmin.service;

import java.util.List;
import java.util.Optional;

import com.mAdmin.repository.CabinetPeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.TrackPeriod;


@Service
public class TrackPeriodServiceImpl extends TimeSpanServiceImpl<TrackPeriod> implements TrackPeriodService {

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    @Override
    public List<TrackPeriod> getAll() {

        return cabinetPeriodRepository.findAll();

    }

    
    @Override
    public Optional<TrackPeriod> getTrackPeriodById(Long id) {

        return cabinetPeriodRepository.findById(id);

    }


}
