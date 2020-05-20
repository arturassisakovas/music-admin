package com.mAdmin.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.mAdmin.repository.AttendeeInSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.AttendeeInSession;


@Service
public class RegisterServiceImpl implements RegisterService {

    
    @Autowired
    private AttendeeService attendeeService;

    
    @Autowired
    private AttendeeInSessionRepository attendeeInSessionRepository;

    
    @Transactional
    public void assignPotentialAttendeeToReservedAttendee(HttpSession session) {

        List<?> registeredAttendeesTemp = (List<?>) session.getAttribute("registeredAttendees");
        List<Attendee> registeredAttendees = new ArrayList<>();

        if (registeredAttendeesTemp != null) {
            registeredAttendeesTemp.removeIf(a -> a == null);
            for (Object attendee : registeredAttendeesTemp) {
                registeredAttendees.add((Attendee) attendee);
            }
        }

         for (Attendee attendee : registeredAttendees) {
             attendeeService.convertAttendeeToReservedAttendee(attendee);
         }
    }

    
    public void deletePotentialAttendee(AttendeeInSession potentialAttendee) {
        attendeeInSessionRepository.deleteById(potentialAttendee.getId());
    }
}
