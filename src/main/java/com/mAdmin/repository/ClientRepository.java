package com.mAdmin.repository;

import com.mAdmin.entity.Agreement;
import com.mAdmin.entity.Client;
import com.mAdmin.enumerator.ClientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface ClientRepository extends JpaRepository<Client, Long> {

    
    Client findByEmail(String email);

    
    Optional<Client> findByConfirmationToken(String confirmationToken);

    
    Page<Client> findByStatusOrderByCreatedAtDesc(ClientStatus status, Pageable pageable);

    
    Page<Client> findByStatusInOrderByCreatedAtDesc(Collection<ClientStatus> statuses, Pageable pageable);

    
    List<Client> findByStatusAndAgreementsContaining(ClientStatus status, Agreement agreement);

    
    Page<Client> findByStatusNot(ClientStatus status, Pageable pageable);

    
    List<Client> findByStatusNot(ClientStatus status);

    
    @Query("select distinct c from Client c join c.attendees a where "
            + "a in (select con.attendee from Contract con where con.validTo = :todayDate)")
    List<Client> findClientsWithExpiringContractToday(@Param("todayDate") LocalDate todayDate);

    
    @Query("select distinct c from Client c, Invoice i, Payment p where c = i.client and "
            + "i.expireDate = :invoiceEndDate and i = p.invoice and p.datePaid is not null")
    List<Client> findClientsForInvoiceEmail(@Param("invoiceEndDate") LocalDate invoiceEndDate);

    
    @Query("select DISTINCT c from Client c Join c.attendees a where c = a.client and "
            + "a.active is false order by c.createdAt desc")
    Page<Client> findAllReservedClients(Pageable pageable);

    
    @Query("select distinct c from Client c  join c.attendees a where "
            + "((select count(con.attendee) from Contract con where con.attendee.client = c "
            + "and con.type = 'ACTIVE') = 0) and c.status = 'ACTIVE'")
    List<Client> findActiveClientsWithNotActiveContract();

    
    @Query("select c from Client c where c.status in :statuses "
            + "and (c.name like %:searchQuery% or c.surname like %:searchQuery% ) order by c.createdAt desc")
    List<Client> findByStatusInWithNameContainingOrSurnameContaining(@Param("statuses") Collection<ClientStatus> status,
            @Param("searchQuery") String searchQuery);

    
    Page<Client> findByStatusIn(Collection<ClientStatus> statuses, Pageable pageable);
}
