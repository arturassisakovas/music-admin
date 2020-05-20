package com.mAdmin.repository;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.AttendeeNonPaid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;


public interface AttendeeNonPaidRepository extends JpaRepository<AttendeeNonPaid, Long> {

    
    @Transactional
    @Modifying
    @Query("delete from AttendeeNonPaid a where a.attendee = ?1")
    void deleteByAttendee(Attendee attendee);

    
    AttendeeNonPaid findByAttendee(Attendee attendee);

    
    AttendeeNonPaid findByAttendeeIn(List<Attendee> attendees);
}
