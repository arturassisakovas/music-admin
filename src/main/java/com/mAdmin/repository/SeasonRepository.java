package com.mAdmin.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mAdmin.entity.Season;


public interface SeasonRepository extends JpaRepository<Season, Long> {

    
    @Query("select s from Season s where s.endDate >= ?1")
    List<Season> findByEndDateAfterOrEqual(LocalDate date);

    
    Season findByName(String name);

    
    @Query("select s from Season s WHERE s.startDate <= ?1 and s.endDate >= ?1")
    Season findActiveSeason(LocalDate today);

    
    Page<Season> findAllByOrderByCreatedAtDesc(Pageable pageable);

    
    Page<Season> findAllBy(Pageable pageable);
}
