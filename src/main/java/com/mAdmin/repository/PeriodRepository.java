package com.mAdmin.repository;

import com.mAdmin.entity.Period;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface PeriodRepository extends JpaRepository<Period, Long> {

    
    @Query("select p from Period p where p.endDate >= ?1 and p.startDate <= ?1")
    Period findByDateBetweenStartAndEndDates(LocalDate date);

    
    @Query("select p from Period p where p.endDate >= ?1")
    List<Period> findByEndDateAfterOrEqual(LocalDate date);

    
    List<Period> findBySeasonId(Long id);

    
    Optional<Period> findFirstBySeasonId(Long id);

    
    List<Period> findAllByName(String name);

    
    List<Period> findAllBySeasonId(Long id);

    
    @Query("SELECT p FROM Period p Join p.season s WHERE"
            + " s.startDate <= :endDate AND s.endDate >= :startDate AND "
            + "p.endDate >= :startDate AND p.startDate <= :endDate"
            + " ORDER BY p.startDate asc")
    List<Period> findPeriodsForFittingSeason(@Param("startDate") LocalDate today,
                                             @Param("endDate") LocalDate nextMonth);

    
    Page<Period> findAllByOrderByCreatedAtDesc(Pageable pageable);

    
    Page<Period> findAll(Pageable pageable);

    
    @Query("SELECT p FROM Period p WHERE p.endDate >= ?1 AND p.startDate <= ?1 OR"
            + " p.startDate >= ?1 AND p.startDate <= ?2")
    List<Period> findPeriodsForRegistration(LocalDate currentDate, LocalDate potentialDate);
}
