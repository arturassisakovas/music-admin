package com.mAdmin.repository;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Pool;
import com.mAdmin.entity.TrackPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface GroupRepository extends JpaRepository<Group, Long> {

    
    @Query("select g from Group g where g.endDate >= ?1 and g.isPublic = true")
    List<Group> findByEndDateAfterOrEqual(LocalDate date);

    
    @Query("select g from Group g JOIN g.employees c "
            + "WHERE c.id = :coachId and g.endDate >= :date and g.isPublic = true")
    List<Group> findByCoachAndEndDateAfterOrEqual(@Param("coachId") Long coachId, @Param("date") LocalDate workoutDate);

    
    @Query("select g from Group g where g.startDate >= ?1 and g.endDate <= ?2")
    List<Group> findByStartDateAndEndDateFitInPeriod(LocalDate startDate, LocalDate endDate);

    
    @Query("select g from Group g join Pool p on (g.pool = p.id) WHERE p.city = :city and g in :groupsList")
    List<Group> filterByCity(@Param("city") String city, @Param("groupsList") List<Group> groupsList);

    
    List<Group> findByTrackPeriodId(Long id);

    
    @Query("select g from Group g JOIN g.employees c WHERE c.id = :coachId and "
            + "g in (select gw.group from GroupWorkout gw WHERE gw.date = :workoutDate)")
    List<Group> findByCoachIdAndWorkoutDate(@Param("coachId") Long id, @Param("workoutDate") LocalDate workoutDate);

    
    @Query("select g from Group g JOIN g.employees c WHERE c.id = :coachId")
    List<Group> findByCoachId(@Param("coachId") Long id);

    
    Optional<Group> findTopByOrderByIdDesc();

    
    @Query("select g from Group g where g.isPublic = true and g.startDate >= ?1 "
            + "and g.endDate <= ?2 order by g.createdAt desc")
    Page<Group> findByIsPublicTrue(LocalDate startDate, LocalDate endDate, Pageable pageable);

    
    @Query("select g from Group g where g.isPublic = true and g.endDate >= ?1 order by g.createdAt desc")
    Page<Group> findByIsPublicTrueAndIntoFuture(LocalDate endDate, Pageable pageable);

    
    @Query("select g from Group g where g.isPublic = true and g.startDate >= :startDate and g.endDate <= :endDate and "
            + "(g.name like %:searchQuery%) order by totalNumOfAttendeesRatio asc")
    List<Group> findByIsPublicTrueAndNameContainingOrdered(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("searchQuery") String searchQuery);

    
    Page<Group> findByIsPublicTrue(Pageable pageable);

    
    @Query("select g from Group g where g.isPublic = true and "
            + "(g.name like %:searchQuery%) order by totalNumOfAttendeesRatio asc")
    List<Group> findByIsPublicTrueAndNameContainingOrdered(String searchQuery);

    
    List<Group> findByIsPublicTrue();

    
    List<Group> findByTrackPeriodAndIsPublicTrue(TrackPeriod tp);

    
    List<Group> findByTrackPeriodInAndIsPublicTrue(List<TrackPeriod> tp);

    
    @Query("select g from Group g Where g.id in " + "(select distinct a.group.id from Attendance a "
            + "where a.attendee = ?1 and a.workoutDate > ?2 and a.workoutDate < ?3)")
    List<Group> findGroupFromAttendanceByAttendeeAndDates(Attendee attendee, LocalDate startDate, LocalDate endDate);

    
    @Query("select g from Group g Where g.startDate >= ?2 and g.endDate <= ?3 and g.trackPeriod in"
            + "(select tp from TrackPeriod tp Where tp.track in"
            + "(select t from Track t where t.pool = ?1))")
    List<Group> findActiveGroupsByProvidedPool(Pool pool, LocalDate startDate, LocalDate endDate);

    
    List<Group> findAllByEndDateEquals(LocalDate endDate);
}
