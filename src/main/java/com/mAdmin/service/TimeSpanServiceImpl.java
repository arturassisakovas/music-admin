package com.mAdmin.service;

import java.time.LocalDate;
import java.util.List;

import com.mAdmin.entity.TimeSpan;
import com.mAdmin.util.DateUtil;

public class TimeSpanServiceImpl<T extends TimeSpan> implements TimeSpanService<T> {

    @Override
    public boolean isDateInPeriod(LocalDate date, T period) {
        if (DateUtil.isAfterOrEqual(date, period.getStartDate())
                && DateUtil.isBeforeOrEqual(date, period.getEndDate())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isPeriodOverlapping(T period, List<T> periods) {
        for (T tp : periods) {
            if (isDateInPeriod(period.getStartDate(), tp) || isDateInPeriod(period.getEndDate(), tp)) {
                return true;
            }
            if (isDateInPeriod(tp.getStartDate(), period) || isDateInPeriod(tp.getEndDate(), period)) {
                return true;
            }
        }

        return false;
    }

}
