package com.mAdmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mAdmin.entity.AttendeeInSession;
import com.mAdmin.entity.Group;


public interface AttendeeInSessionRepository extends JpaRepository<AttendeeInSession, Long> {

    
    int countByGroup(Group group);

     
    AttendeeInSession findByReservedClientSessionId(String reservedClientSessionId);
}
