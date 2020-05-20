package com.mAdmin.service;

import com.mAdmin.entity.AttendeeInSession;
import com.mAdmin.entity.Group;


public interface AttendeeInSessionService {

    
    AttendeeInSession create(Group group, String sessionId);

}
