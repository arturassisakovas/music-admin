package com.mAdmin.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mAdmin.repository.DepartmentNonWorkdayRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.PoolNonWorkday;
import com.mAdmin.entity.TrackWorkingHour;


@Service
public class TrackWorkingHourServiceImpl implements TrackWorkingHourService {

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private DepartmentNonWorkdayRepository departmentNonWorkdayRepository;

    
    public List<TrackWorkingHour> getAll() {

        return cabinetWorkingHourRepository.findAll();

    }

    
    public Optional<TrackWorkingHour> getTrackWorkingHourById(Long id) {

        return cabinetWorkingHourRepository.findById(id);

    }

    
    public List<LocalDate> getAllWorkoutDates(LocalDate startDate, LocalDate endDate, int weekDay) {

        List<LocalDate> workoutDays = new ArrayList<LocalDate>();
        while (startDate.isBefore(endDate.plusDays(1))) {
            if (startDate.getDayOfWeek().getValue() - 1 == weekDay) {
                workoutDays.add(startDate);
            }
            startDate = startDate.plusDays(1);
        }
        return workoutDays;
    }

    
    public List<LocalDate> getWorkoutDatesWithoutNonWorkdays(List<LocalDate> workoutDays) {
        List<PoolNonWorkday> poolNonWorkdays = departmentNonWorkdayRepository.findAll();
        List<LocalDate> nonWorkdays = poolNonWorkdays.stream()
                                                     .map(PoolNonWorkday::getDate)
                                                     .collect(Collectors.toList());
        workoutDays.removeAll(nonWorkdays);
        return workoutDays;
    }
}
