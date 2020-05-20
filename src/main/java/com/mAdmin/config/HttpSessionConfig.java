package com.mAdmin.config;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.mAdmin.entity.AttendeeInSession;
import com.mAdmin.repository.AttendeeInSessionRepository;
import com.mAdmin.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HttpSessionConfig {

    
    @Autowired
    private AttendeeInSessionRepository attendeeInSessionRepository;

    
    @Autowired
    private RegisterService registerService;

    
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {

            @Override
            public void sessionCreated(HttpSessionEvent se) {

            }

            @Override
            public void sessionDestroyed(HttpSessionEvent sessionEvent) {
                HttpSession session = sessionEvent.getSession();
                if (session.getAttribute("attendeeWrittenToDb") != null) {
                    registerService.assignPotentialAttendeeToReservedAttendee(session);
                } else {
                    AttendeeInSession potentialAttendee = attendeeInSessionRepository.
                            findByReservedClientSessionId(sessionEvent.getSession().getId());
                    if (potentialAttendee != null) {
                        registerService.deletePotentialAttendee(potentialAttendee);
                    }
                }
            }
        };
    }
}
