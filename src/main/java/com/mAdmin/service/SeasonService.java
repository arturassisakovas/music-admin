package com.mAdmin.service;

import java.time.LocalDate;
import java.util.List;

import com.mAdmin.repository.SeasonRepository;
import com.mAdmin.entity.Season;


public interface SeasonService extends TimeSpanService<Season> {

    
    List<Season> getAllAvailable();

    
    Boolean checkIfDatesFitIntoSeason(Season season, LocalDate startDate, LocalDate endDate);

}
