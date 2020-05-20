package com.mAdmin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mAdmin.entity.Discount;
import com.mAdmin.entity.Period;


public interface DiscountRepository extends JpaRepository<Discount, Long> {

    
    List<Discount> findByPeriod(Period period);

    
    void deleteByPeriodId(Long id);

    
    void deleteByPeriod(Period period);

    
    List<Discount> findAllByPeriodId(Long id);

    
    Discount findByPeriodAndSubscriptionMonths(Period period, int numberOfWorkouts);

}
