package com.mAdmin.repository;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Transactional
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

    
    List<Attendee> findByClient(Client client);

    
    List<Attendee> findByGroup(Group group);

    
    @Query("select a from Attendee a where a.active = true and (((:monthFrom != :monthTill) and"
            + " ((month(a.birthDate) = :monthFrom and day(a.birthDate) >= :dayFrom)"
            + " or (month(a.birthDate) = :monthTill and day(a.birthDate) <= :dayTill )))"
            + " or (month(a.birthDate) = :monthFrom and month(a.birthDate) = :monthTill and (day(a.birthDate)"
            + " between :dayFrom and :dayTill)))")
    List<Attendee> findByBirthdayDateIntervalAndActiveTrue(@Param("monthFrom") int monthFrom,
                                                           @Param("dayFrom") int dayFrom,
                                                           @Param("monthTill") int monthTill,
                                                           @Param("dayTill") int dayTill);

    
    Page<Attendee> findByActiveFalseOrderByCreatedAtDesc(Pageable pageable);

    
    @Query("select a from Attendee a where a.client.id = ?1 and a.id not in ("
            + "select c.attendee.id from Contract c where c.type = 'ACTIVE')")
    List<Attendee> findAttendeeWithoutContractByClient(long clientId);

    
    @Query("select a from Attendee a where a.group in (select g from "
            + "Group g JOIN g.employees c WHERE c = :coach) order by workoutStartDate asc")
    List<Attendee> findByCoachOrderByFirstWorkoutDateAscending(Employee coach);

    
    List<Attendee> findByNewGroup(Group group);

    
    @Query("select a from Attendee a WHERE a.group = ?1 and a.workoutStartDate <= ?2")
    List<Attendee> findByGroupAndWorkoutStartDateLessThanQuery(Group group, LocalDate date);

    
    List<Attendee> findByIsMissingTrue();

    
    List<Attendee> findAllByActiveTrue();

    
    Page<Attendee> findByActiveFalse(Pageable pageable);
}
