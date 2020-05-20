package com.mAdmin.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.mAdmin.enumerator.InvoiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.Invoice;


public interface InvoiceRepository extends JpaRepository<Invoice, Long>, RepositoryCustom<Invoice> {

    
    @Query("select count(*) from Invoice i where i.createdAt >= :dateTime")
    int countByCreatedAtAfterOrEqual(@Param("dateTime") LocalDateTime dateTime);

    
    @Query("select i from Invoice i Where i.expireDate = ?1 ORDER BY i.client")
    List<Invoice> findAllExpiringInvoicesAndOrderByClient(LocalDate localDate);

    
    @Query("select i from Invoice i Where i.client = ?1")
    Page<Invoice> findAllByClients(Pageable pageable, Client client);

    
    Page<Invoice> findByClientAndValidTrueOrderByCreatedAtDesc(Client client, Pageable pageable);

    
    List<Invoice> findAllByValidTrueAndExpireDateEquals(LocalDate fifteenthOfTheMotnh);

    
    List<Invoice> findByExpireDateEquals(LocalDate date);

    
    List<Invoice> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    
    @Query("select i from Invoice i, Payment p where i.createdAt >= ?1 and i.createdAt <= ?2 "
            + "and p.invoice = i and p.datePaid is not null and p.status = true")
    List<Invoice> findByCreatedAtBetweenAndPaid(LocalDateTime startDate, LocalDateTime endDate);

    
    Invoice findTopByCreatedAtBeforeOrderByIdDesc(LocalDateTime dateTime);

    
    List<Invoice> findByExpireDateAndTypeIsNotAndValidIsTrue(LocalDate withDayOfMonth, InvoiceType invoiceType);

    
    List<Invoice> findByExpireDateAndType(LocalDate date, InvoiceType invoiceType);

    
    Invoice findTopByClientAndDocumentPathIsNullOrderByIdDesc(Client client);



    
    Invoice findFirstByTypeEqualsAndExpireDateEquals(InvoiceType invoiceType, LocalDate date);

    
    Page<Invoice> findByClientAndValidTrue(Client client, Pageable pageable);
}
