package com.mAdmin.repository;

import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface PaymentRepository extends JpaRepository<Payment, Long> {

    
    Payment findByInvoiceAndDatePaidNotNull(Invoice invoice);

    
    Payment findFirstByInvoiceOrderByIdDesc(Invoice invoice);

    
    Payment findByInvoiceAndStatusTrue(Invoice invoice);

    
    List<Payment> findByDatePaidIsNotNullAndCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
