package com.mAdmin.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.mAdmin.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;


public interface InvoiceRecordRepository extends JpaRepository<InvoiceRecord, Long> {

    
    InvoiceRecord findTopByContractOrderByIdDesc(Contract contract);

    
    List<InvoiceRecord> findByContract(Contract contract);

    
    InvoiceRecord findByContractAndAttendeeAndPriceIsNot(Contract contract, Attendee attendee, BigDecimal price);

    
    InvoiceRecord findByContractAndPrice(Contract contract, BigDecimal price);

    
    List<InvoiceRecord> findByInvoiceIn(List<Invoice> invoices);

    
    @Query(value = "select i from InvoiceRecord i where "
            + "i.periodStartDate <= ?1 and i.periodEndDate >= ?2 and i.invoice in ("
            + " select p.invoice from Payment p where p.datePaid is not null and p.status = true)")
    List<InvoiceRecord> findByStartDateAndEndDateFitInPeriod(LocalDate startDate, LocalDate endDate);

    
    InvoiceRecord findByInvoiceAndAttendee(Invoice invoice, Attendee attendee);

    
    InvoiceRecord findFirstByInvoice(Invoice invoice);

    
    List<InvoiceRecord> findByContractIn(List<Contract> contracts);

    
    List<InvoiceRecord> findByGroup(Group group);

    
    InvoiceRecord findFirstByContractEqualsOrderByIdDesc(Contract contract);
}
