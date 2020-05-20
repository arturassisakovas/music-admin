package com.mAdmin.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mAdmin.entity.Track;



public interface CabinetRepository extends JpaRepository<Track, Long> {

    
    Optional<Track> findFirstByPoolId(Long id);

    
    List<Track> findByPoolId(Long id);

    
    List<Track> findAllByNumber(int number);

    
    @Query("select t from Track t JOIN t.trackPeriods tp WHERE tp.id = ?1")
    Track findByTrackPeriodId(Long id);

    
    @Query("SELECT t FROM Track t JOIN t.trackPeriods tp "
            + "WHERE t.pool.id = ?1 AND tp.startDate >= ?2 AND tp.endDate <= ?3")
    List<Track> findByPoolIdAndSeasonPeriod(Long id, LocalDate startDate, LocalDate endDate);

}
