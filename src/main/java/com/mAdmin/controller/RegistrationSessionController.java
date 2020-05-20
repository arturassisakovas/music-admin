package com.mAdmin.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.mAdmin.repository.AttendeeInSessionRepository;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.AttendeeInSession;
import com.mAdmin.entity.Subscription;
import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


@Controller
@Scope("session")
public class RegistrationSessionController {

    
    private static final int SESSION_TIMEOUT = 30;

    
    private static final int MINUTE_IN_SECONDS = 60;

    
    public void onLoad() {
        resetSessionTimer();
    }

    
    @Autowired
    private ClientRegistrationController clientRegistrationController;

    
    @Autowired
    private AttendeeInSessionRepository attendeeInSessionRepository;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    public void resetSessionTimer() {
        LocalDateTime currentDate = LocalDateTime.now();
        String startDate = currentDate.toString();
        String endDate = currentDate.plusMinutes(SESSION_TIMEOUT).toString();

        PrimeFaces.current().executeScript("prepareSessionCountdown('" + startDate + "', '" + endDate + "')");

        FacesContext.getCurrentInstance().getExternalContext()
        .setSessionMaxInactiveInterval(SESSION_TIMEOUT * MINUTE_IN_SECONDS);

        Integer regStepInSession = Faces.getSessionAttribute("currentRegistrationStep");

        boolean isStep4Point2 = checkIfFourPointTwo(clientRegistrationController.collectRegisteredAttendees());
        if (regStepInSession != null) {
            final int stepFour = 4;
            if (regStepInSession < stepFour || !isStep4Point2) {
                String sessionId = FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
                AttendeeInSession attendeeInSession = attendeeInSessionRepository
                        .findByReservedClientSessionId(sessionId);
                if (attendeeInSession != null) {
                    attendeeInSession.setCreatedAt(LocalDateTime.now());
                    attendeeInSessionRepository.save(attendeeInSession);
                }
            }
            List<?> registeredAttendeesTemp = Faces.getSessionAttribute("registeredAttendees");
            List<Attendee> registeredAttendees = new ArrayList<>();
            if (registeredAttendeesTemp != null && !registeredAttendeesTemp.isEmpty()) {
                for (Object attendee : registeredAttendeesTemp) {
                    if (attendee != null) {
                        ((Attendee) attendee).setUpdatedAt(LocalDateTime.now());
                        registeredAttendees.add((Attendee) attendee);
                    }
                }
                attendeeRepository.saveAll(registeredAttendees);
                List<Subscription> subscriptionsToUpdate = new ArrayList<>();
                for (Attendee attendee : registeredAttendees) {
                    Subscription tempSubscription = subscriptionRepository.findByAttendeeAndInvoiceIsNull(attendee);
                    if (tempSubscription != null) {
                        tempSubscription.setUpdatedAt(LocalDateTime.now());
                        subscriptionsToUpdate.add(tempSubscription);
                    }
                }
                if (!subscriptionsToUpdate.isEmpty()) {
                    subscriptionRepository.saveAll(subscriptionsToUpdate);
                }
            }
        }
    }

    
    private boolean checkIfFourPointTwo(List<Attendee> attendees) {
        boolean fourPointTwo = false;
        for (Attendee attendee : attendees) {
            if (attendee != null) {
                fourPointTwo = true;
                break;
            }
        }
        return fourPointTwo;
    }

    
    public int getSessionTimeout() {
        return SESSION_TIMEOUT;
    }


}
