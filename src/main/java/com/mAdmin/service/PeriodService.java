package com.mAdmin.service;

import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.entity.Period;

import java.time.LocalDate;
import java.util.List;


public interface PeriodService extends TimeSpanService<Period> {

    
    List<Period> getAllAvailable();

    
    Boolean checkIfDatesFitIntoPeriod(Period period, LocalDate startDate, LocalDate endDate);

    
    Period getPeriodForFittingSeason(LocalDate startDate, LocalDate endDate);

}
