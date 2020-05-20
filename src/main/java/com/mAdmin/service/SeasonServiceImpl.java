package com.mAdmin.service;

import java.time.LocalDate;
import java.util.List;

import com.mAdmin.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Season;


@Service
public class SeasonServiceImpl extends TimeSpanServiceImpl<Season> implements SeasonService {

    
    @Autowired
    private SeasonRepository seasonRepository;

    
    @Override
    public List<Season> getAllAvailable() {

        LocalDate currentDate = LocalDate.now();

        return seasonRepository.findByEndDateAfterOrEqual(currentDate);

    }

    
    @Override
    public Boolean checkIfDatesFitIntoSeason(Season season, LocalDate startDate, LocalDate endDate) {

        LocalDate seasonStart = season.getStartDate();
        LocalDate seasonEnd = season.getEndDate();

        if (seasonStart.isBefore(startDate) || seasonStart.isEqual(startDate)) {
            if (seasonEnd.isAfter(endDate) || seasonEnd.isEqual(endDate)) {
                return true;
            }
        }

        return false;
    }

}
