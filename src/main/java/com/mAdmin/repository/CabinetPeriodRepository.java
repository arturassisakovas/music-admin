package com.mAdmin.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mAdmin.entity.Group;
import com.mAdmin.entity.Track;
import com.mAdmin.entity.TrackPeriod;


public interface CabinetPeriodRepository extends JpaRepository<TrackPeriod, Long> {

    
    @Query("select tp from TrackPeriod tp where tp in "
            + "(select g.trackPeriod from Group g where g in (:groupsList))")
    List<TrackPeriod> findByGroupsList(
            @Param("groupsList") List<Group> groupsList);

    
    List<TrackPeriod> findByTrackId(Long id);

    
    Optional<TrackPeriod> findFirstByTrackId(Long id);

    
    List<TrackPeriod> findByTrack(Track track);

    
    Page<TrackPeriod> findByStartDateBeforeAndEndDateAfterOrderByCreatedAtDesc(Pageable pageable,
                                                                               LocalDate endDate, LocalDate startDate);

    
    long findTrackIdById(Long id);

    
    List<TrackPeriod> findAllByStartDateBeforeAndEndDateAfter(LocalDate endDate, LocalDate startDate);

    
    @Query("select g.trackPeriod from Group g WHERE g.isPublic = true")
    List<TrackPeriod> findTrackPeriod();

    
    @Query("SELECT tp FROM TrackPeriod tp WHERE tp.track.id = ?1 "
            + "AND tp.startDate >= ?2 AND tp.endDate <= ?3")
    List<TrackPeriod> findByTrackIdAndSeasonPeriod(
            Long id, LocalDate startDate, LocalDate endDate);

    
    Page<TrackPeriod> findAllByOrderByCreatedAtDesc(Pageable pageable);

    
    Page<TrackPeriod> findByStartDateBeforeAndEndDateAfter(Pageable pageable, LocalDate endDate, LocalDate startDate);
}
