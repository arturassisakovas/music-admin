package com.mAdmin.service;

import java.util.List;

import com.mAdmin.enumerator.PeriodType;
import com.mAdmin.entity.Discount;
import com.mAdmin.entity.Period;


public interface DiscountService {

    
    int getDiscountRateBySubscriptionMonths(Period period, int subscriptionMonths);

    
    int getDiscountRateBySubscriptionMonths(Long periodId, int subscriptionMonths);

    
    Long getIdBySubscriptionMonths(Period period, int subscriptionMonths);

    
    List<Discount> prepareDefaultDiscountsList(PeriodType periodType);

}
