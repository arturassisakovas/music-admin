package com.mAdmin.service;

import java.util.List;
import java.util.Optional;

import com.mAdmin.repository.CabinetWeekdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.TrackWeekday;


@Service
public class TrackWeekdayServiceImpl {

    
    @Autowired
    private CabinetWeekdayRepository cabinetWeekdayRepository;

    
    public List<TrackWeekday> getAll() {

        return cabinetWeekdayRepository.findAll();

    }

    
    public Optional<TrackWeekday> getTrackWeekdayById(Long id) {

        return cabinetWeekdayRepository.findById(id);

    }

}
