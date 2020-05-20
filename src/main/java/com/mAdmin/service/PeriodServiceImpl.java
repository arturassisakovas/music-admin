package com.mAdmin.service;

import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.entity.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class PeriodServiceImpl extends TimeSpanServiceImpl<Period> implements PeriodService {

    
    @Autowired
    private PeriodRepository periodRepository;

    
    @Override
    public List<Period> getAllAvailable() {

        LocalDate currentDate = LocalDate.now();

        return periodRepository.findByEndDateAfterOrEqual(currentDate);

    }

    
    @Override
    public Boolean checkIfDatesFitIntoPeriod(Period period, LocalDate startDate, LocalDate endDate) {

        LocalDate periodStart = period.getStartDate();
        LocalDate periodEnd = period.getEndDate();

        if (periodStart.isBefore(startDate) || periodStart.isEqual(startDate)) {
            if (periodEnd.isAfter(endDate) || periodEnd.isEqual(endDate)) {
                return true;
            }
        }

        return false;
    }

    
    @Override
    public Period getPeriodForFittingSeason(LocalDate startDate, LocalDate endDate) {
        List<Period> periods = periodRepository.findPeriodsForFittingSeason(startDate, endDate);
        if (!periods.isEmpty()) {
            return periods.get(0);
        } else {
            return null;
        }
    }

}
