package com.mAdmin.service;

import com.mAdmin.enumerator.WorkoutsPerWeek;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Discount;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Subscription;
import com.mAdmin.model.SubscriptionCreationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface SubscriptionService {

    
    Subscription create(int subscriptionMonths, WorkoutsPerWeek workoutsPerWeek, BigDecimal price, LocalDate subStart,
                        LocalDate subEnd, Attendee attendee);

    
    Subscription update(Subscription subscription, int subscriptionMonths, WorkoutsPerWeek workoutsPerWeek,
            BigDecimal price, LocalDate subStart, LocalDate subEnd);

    
    List<Integer> subscriptionNumberOfMonthsList(Period period, int[] schoolSubscriptionsArray,
                                                 int[] summerSubscriptionsArray);

    
    long calculateMonthDiff(List<Group> groupsByPool);

    
    void createSubscription(SubscriptionCreationModel subscriptionCreationModel);

    
    List<Subscription> makeSubscriptionReminderList(List<Subscription> subscriptions);

    
    BigDecimal getSubscriptionPrice(Attendee attendee, int lessonCount, Discount discount, Period period);

    
    void deleteSubscriptionsByInvoice(Invoice invoice);

}
