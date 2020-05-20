package com.mAdmin.database;

import java.util.List;

import com.mAdmin.repository.AttendeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mAdmin.entity.Attendee;


@Component
public class AttendeeSeeder {

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    private final Logger logger = LoggerFactory.getLogger(AttendeeSeeder.class);

    
     protected void seedAttendeeTable() {

        List<Attendee> attendees = attendeeRepository.findAll();
        final String loggerText = Attendee.class.getSimpleName() + " table seeding is not required";

        if (attendees.isEmpty()) {
            logger.info(loggerText);
            return;
        }

        attendees.forEach(i -> {
            if (i.getWorkoutStartDate() == null) {
                i.setWorkoutStartDate(i.getCreatedAt().toLocalDate());
            }
        });

        attendeeRepository.saveAll(attendees);
     }

}
