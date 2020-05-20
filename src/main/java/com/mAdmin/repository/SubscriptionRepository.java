package com.mAdmin.repository;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    
    @Query("select s from Subscription s Where s.invoice IN :invoices")
    List<Subscription> findAllByInvoices(@Param("invoices") List<Invoice> invoices);

    
    List<Subscription> findByInvoice(Invoice invoice);

    
    @Query("select s from Subscription s JOIN s.attendee a Join a.contracts ct Where"
            + " ct is not null and s.invoice = ?1")
    List<Subscription> findByInvoiceAndAttendeeTopContractIsActive(Invoice invoice);

    
    List<Subscription> findByStartDateEquals(LocalDate now);

    
    @Query("select s from Subscription s Where s.startDate >= ?1 AND s.endDate <= ?2 and s.endDate >= ?3")
    List<Subscription> findByPeriodDates(LocalDate getStartDate, LocalDate getEndDate, LocalDate now);

    
    @Query("select s from Subscription s JOIN s.attendee a Where"
            + " s.endDate = ?1 and a in (select c.attendee from Contract c where c.validFrom <= s.endDate"
            + " and c.validTo >= s.startDate and c.type = 'ACTIVE') ORDER BY a.client")
    List<Subscription> findBySubscriptionStartDateAndContractTypeActive(LocalDate date);

    
    Subscription findFirstByAttendeeAndStartDateBeforeAndEndDateAfter(Attendee attendee, LocalDate periodDateTo,
            LocalDate periodDateFrom);

    
    Optional<Subscription> findFirstByInvoice(Invoice invoice);

    
    List<Subscription> findByStartDateEqualsOrderByInvoice(LocalDate now);

    
    Subscription findFirstByAttendeeOrderByEndDateDesc(Attendee attendee);

    
    Subscription findByAttendeeAndInvoice(Attendee attendee, Invoice invoice);

    
    Subscription findByAttendeeAndInvoiceIsNull(Attendee attendee);

    
    List<Subscription> findByInvoiceIsNull();
}
