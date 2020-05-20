package com.mAdmin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mAdmin.enumerator.PeriodType;
import com.mAdmin.repository.DiscountRepository;
import com.mAdmin.repository.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Discount;
import com.mAdmin.entity.Period;


@Service
public class DiscountServiceImpl implements DiscountService {

    
    static final int SUBS_MONTHS_2 = 2, SUBS_MONTHS_3 = 3;

    
    @Autowired
    private DiscountRepository discountRepository;

    
    @Autowired
    private PeriodRepository periodRepository;

    
    @Override
    public int getDiscountRateBySubscriptionMonths(Period period, int subscriptionMonths) {
        List<Discount> periodDiscounts = discountRepository.findByPeriod(period);
        int discountRate = 0;
        for (Discount discount : periodDiscounts) {
            if (discount.getSubscriptionMonths() == subscriptionMonths) {
                discountRate = discount.getDiscountRate();
            }
        }
        return discountRate;
    }

    
    @Override
    public int getDiscountRateBySubscriptionMonths(Long periodId, int subscriptionMonths) {
        Optional<Period> maybePeriod = periodRepository.findById(periodId);
        if (maybePeriod.isPresent()) {
            return getDiscountRateBySubscriptionMonths(maybePeriod.get(), subscriptionMonths);
        }
        return 0;
    }

    
    @Override
    public Long getIdBySubscriptionMonths(Period period, int subscriptionMonths) {
        List<Discount> periodDiscounts = discountRepository.findByPeriod(period);
        Long discountId = null;
        for (Discount discount : periodDiscounts) {
            if (discount.getSubscriptionMonths() == subscriptionMonths) {
                discountId = discount.getId();
            }
        }
        return discountId;
    }

    
    @Override
    public List<Discount> prepareDefaultDiscountsList(PeriodType periodType) {
        List<Discount> defaultDiscounts = new ArrayList<Discount>();
        if (periodType == PeriodType.REGULAR) {
            defaultDiscounts.add(new Discount(SUBS_MONTHS_3));
        } else if (periodType == PeriodType.SUMMER) {
            defaultDiscounts.add(new Discount(SUBS_MONTHS_2));
            defaultDiscounts.add(new Discount(SUBS_MONTHS_3));
        }
        return defaultDiscounts;
    }
}
