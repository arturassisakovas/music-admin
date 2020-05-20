package com.mAdmin.service;

import com.mAdmin.enumerator.PeriodType;
import com.mAdmin.enumerator.WorkoutsPerWeek;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.repository.CabinetPeriodRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Discount;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Subscription;
import com.mAdmin.entity.TrackPeriod;
import com.mAdmin.model.SubscriptionCreationModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    @Autowired
    private SubscriptionGenerationService subscriptionGenerationService;

    
    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public Subscription create(int subscriptionMonths, WorkoutsPerWeek workoutsPerWeek, BigDecimal price,
                               LocalDate subStart, LocalDate subEnd, Attendee attendee) {

        Subscription subscription = new Subscription();
        subscription.setNumberOfMonths(subscriptionMonths);
        subscription.setWorkoutsPerWeek(workoutsPerWeek);
        subscription.setPrice(price);
        subscription.setStartDate(subStart);
        subscription.setEndDate(subEnd);
        subscription.setAttendee(attendee);

        subscription = subscriptionRepository.save(subscription);

        return subscription;
    }

    @Override
    public Subscription update(Subscription subscription, int subscriptionMonths, WorkoutsPerWeek workoutsPerWeek,
            BigDecimal price, LocalDate subStart, LocalDate subEnd) {

        subscription.setNumberOfMonths(subscriptionMonths);
        subscription.setWorkoutsPerWeek(workoutsPerWeek);
        subscription.setPrice(price);
        subscription.setStartDate(subStart);
        subscription.setEndDate(subEnd);

        subscription = subscriptionRepository.save(subscription);

        return subscription;
    }

    @Override
    public List<Integer> subscriptionNumberOfMonthsList(Period period, int[] schoolSubscriptionsArray,
                                                        int[] summerSubscriptionsArray) {
        if (period.getPeriodType() == PeriodType.REGULAR) {
            return Arrays.stream(schoolSubscriptionsArray).boxed().collect(Collectors.toList());
        } else {
            return Arrays.stream(summerSubscriptionsArray).boxed().collect(Collectors.toList());
        }
    }

    @Override
    public long calculateMonthDiff(List<Group> groupsByPool) {
        List<TrackPeriod> availableTrackPeriods = cabinetPeriodRepository.findByGroupsList(groupsByPool);
        Optional<LocalDate> optionalMaxDate = availableTrackPeriods.stream().
                map(TrackPeriod::getEndDate).max(LocalDate::compareTo);
        if (optionalMaxDate.isPresent()) {
            LocalDate maxDate = optionalMaxDate.get();

            return ChronoUnit.MONTHS.between(LocalDate.now().withDayOfMonth(1), maxDate.withDayOfMonth(1));
        } else {
            return 1;
        }
    }

    
    @Override
    public void createSubscription(SubscriptionCreationModel subscriptionCreationModel) {

        Subscription subscription = subscriptionCreationModel.getSubscription();
            subscription.setStartDate(subscriptionCreationModel.getStartDate());

            subscription.setEndDate(subscriptionCreationModel.getExpireDate());

            subscription.setNumberOfMonths(subscriptionCreationModel.getMonths());
            subscription.setPrice(subscriptionCreationModel.getPrice());

            subscription.setWorkoutsPerWeek(subscriptionCreationModel.getWorkoutsPerWeek());

            subscription.setInvoice(subscriptionCreationModel.getInvoice());
            subscriptionRepository.saveAndFlush(subscription);
    }

    @Override
    public List<Subscription> makeSubscriptionReminderList(List<Subscription> subscriptions) {
        long monthBetween;
        List<Subscription> subscriptionsReminder = new ArrayList<>();
        final int fifteen = 15;
        final int thirty = 30;
        final int one = 1;
        final int two = 2;
        final int three = 3;

        for (Subscription s : subscriptions) {
            if (s.getStartDate().getMonthValue() == s.getEndDate().getMonthValue()) {
                monthBetween = ChronoUnit.MONTHS.between(s.getStartDate(), s.getEndDate());
                monthBetween++;
            } else {
                monthBetween = ChronoUnit.MONTHS.between(s.getStartDate(), s.getEndDate());
                monthBetween++;
            }

            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), s.getEndDate());
            if (daysBetween <= fifteen && monthBetween == one) {
                subscriptionsReminder.add(s);
            } else {
                if (daysBetween <= thirty && (monthBetween == three || monthBetween == two)) {
                    subscriptionsReminder.add(s);
                }
            }
        }
        return subscriptionsReminder;
    }

    @Override
    public BigDecimal getSubscriptionPrice(Attendee attendee, int lessonCount, Discount discount, Period period) {
        final int oneHundred = 100;
        BigDecimal totalPrice;
        final int numbOfAttendees = 6;
        if (discount == null) {
            if ((attendee.getGroup()).getNumOfAttendees() == numbOfAttendees) {
                totalPrice = (period.getSeason()).getSmallGroupPrice()
                        .multiply(new BigDecimal(lessonCount));
            } else {
                totalPrice = (period.getSeason()).getBigGroupPrice()
                        .multiply(new BigDecimal(lessonCount));
            }
        } else {

            double discountRate = ((double) (oneHundred - discount.getDiscountRate())
                    / (double) oneHundred);

            if ((attendee.getGroup()).getNumOfAttendees() == numbOfAttendees) {
                totalPrice = period.getSeason().getSmallGroupPrice()
                        .multiply(new BigDecimal(lessonCount)).multiply(new BigDecimal(discountRate));
            } else {
                totalPrice = period.getSeason().getBigGroupPrice()
                        .multiply(new BigDecimal(lessonCount)).multiply(new BigDecimal(discountRate));
            }
        }
        return totalPrice;
    }

    @Override
    public void deleteSubscriptionsByInvoice(Invoice invoice) {
        if (invoice != null) {
            List<Subscription> subscriptions = subscriptionRepository.findByInvoice(invoice);
            subscriptionRepository.deleteAll(subscriptions);

            if (!subscriptions.isEmpty()) {
                logger.info("------------------ Subscriptions of Invoice ID " + invoice.getId() + " were deleted "
                        + "----------------------------------");
            }
        }
    }
}
