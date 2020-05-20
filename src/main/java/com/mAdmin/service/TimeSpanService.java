package com.mAdmin.service;

import java.time.LocalDate;
import java.util.List;

public interface TimeSpanService<T>  {

    
    boolean isDateInPeriod(LocalDate date, T period);

    
    boolean isPeriodOverlapping(T period, List<T> trackPeriods);

}
