package com.mAdmin.service;

import com.mAdmin.repository.AttendeeInSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.AttendeeInSession;
import com.mAdmin.entity.Group;


@Service
public class AttendeeInSessionServiceImpl implements AttendeeInSessionService {

    
    @Autowired
    private AttendeeInSessionRepository attendeeInSessionRepository;

    @Override
    public AttendeeInSession create(Group group, String sessionId) {
        checkSessionIdPresence(sessionId);
        AttendeeInSession attendeeInSession = new AttendeeInSession();

        attendeeInSession.setGroup(group);
        attendeeInSession.setReservedClientSessionId(sessionId);

        attendeeInSession = attendeeInSessionRepository.save(attendeeInSession);

        return attendeeInSession;
    }

    
    private void checkSessionIdPresence(String sessionId) {
        AttendeeInSession attendeeInSession = attendeeInSessionRepository
                .findByReservedClientSessionId(sessionId);
        if (attendeeInSession != null) {
            attendeeInSessionRepository.deleteById(attendeeInSession.getId());
        }
    }
}
