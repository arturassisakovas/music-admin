package com.mAdmin.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mAdmin.enumerator.InvoiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.enumerator.ContractType;


public interface ContractRepository extends JpaRepository<Contract, Long> {

    
    Contract findByAttendeeAndType(Attendee attendee, ContractType type);

    
    List<Contract> findAllByAttendeeAndType(Attendee attendee, ContractType type);

    
    Contract findTopByAttendeeAndTypeOrderByIdDesc(Attendee attendee, ContractType type);

    
    Contract findTopByAttendeeOrderByIdDesc(Attendee attendee);

    
    Contract findByAttendeeAndValidFromBeforeAndValidToAfterAndType(Attendee attendee, LocalDate localDate,
            LocalDate localDate2, ContractType type);

    
    @Query("select c from Contract c Where c.validTo >= ?1")
    List<Contract> findAllValidContracts(LocalDate today);

    
    @Query("select c from Contract c Join c.attendee a Where "
            + "c.validTo = ?1 and c.type = ?2 and a.newGroup is null ORDER BY a.client")
    List<Contract> findByValidToAndTypeAndAttendeeNewGroupNull(LocalDate yesterday, ContractType type);

    
    List<Contract> findByAttendeeInAndTypeEquals(List<Attendee> attendees, ContractType contractType);

    
    @Query("select c from Contract c Join c.attendee a Where a.client = ?1 and c.type != ?2 order by c.createdAt desc")
    Page<Contract> findAllByClientsAndTypeNot(Pageable pageable, Client client, ContractType type);

    
    @Query("select c from Contract c Join c.attendee a Where "
            + "c.terminationDate = ?1 and a.newGroup is null ORDER BY a.client")
    List<Contract> findByTerminationDateAndAttendeeNewGroupIsNullAndOrderByClient(LocalDate now);

    
    @Query("select c from Contract c Join c.attendee a Where "
            + "c.terminationDate = ?1 and a.newGroup != null ORDER BY a.client")
    List<Contract> findByTerminationDateAndAttendeeNewGroupNotNullAndOrderByClient(LocalDate now);

    
    @Query("select count(*) from Contract c where c.createdAt >= :dateTime")
    int countByCreatedAtAfterOrEqual(@Param("dateTime") LocalDateTime dateTime);

    
    List<Contract> findByAttendee(Attendee attendee);

    
    List<Contract> findByValidToAfterAndValidFromBeforeAndTypeNotAndTypeNotLikeAndValidToAfterOrderByAttendee(
            LocalDate startDate, LocalDate endDate, ContractType type, ContractType type1, LocalDate startAfterEnd);

    
    List<Contract> findByValidToAfterAndValidFromBeforeAndAttendeeOrderByIdDesc(LocalDate startDate,
             LocalDate endDate, Attendee attendee);

    
    List<Contract> findByValidTo(LocalDate date);

    
    List<Contract> findByValidFromAndTerminationDateIsNull(LocalDate date);

    
    Contract findTopByAttendeeOrderByValidToDesc(Attendee attendee);

    
    Optional<Contract> findTopByOrderByCreatedAt();

    
    List<Contract> findByValidFromAfterAndValidToBeforeAndTypeLikeOrderByAttendee(LocalDate startDate,
            LocalDate endDate, ContractType type);

    
    Contract findTopByCreatedAtBeforeOrderByIdDesc(LocalDateTime dateTime);

    
    Contract findByValidToAndAttendeeAndType(LocalDate expDate, Attendee attendee, ContractType contractType);

    
    List<Contract> findByAttendeeAndTypeNot(Attendee attendee, ContractType contractType);

    
    List<Contract> findAllByTypeEqualsAndIsSignedFalseAndCreatedAtIsAfter(
            ContractType contractType, LocalDateTime localDate);

    
    List<Contract> findAllByAttendeeInAndTypeEqualsAndIsSignedIsFalse(
            Set<Attendee> setOfAttendees, ContractType notActive);

    
    @Query("select c from Contract c Join c.attendee a join c.invoiceRecords ir Join ir.invoice i Where "
            + "c.terminationDate = ?1 and a.newGroup is not null and i.type = ?2 ORDER BY a.client")
    List<Contract> findByTerminationDateAndAttendeeNewGroupAndInvoiceTypeTerminationAndOrderByClient(
            LocalDate now, InvoiceType invoiceType);

}
