package com.mAdmin.service;

import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Employee;
import com.mAdmin.model.AttendeeCreationModel;
import org.springframework.util.MultiValueMap;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface AttendeeService {

    
    List<Attendee> getAll();

    
    Optional<Attendee> getById(Long id);

    
    String validateAttribute(String attribute);

    
    List<Attendee> getAllByNextBirthdayFromNow(int monthFrom, int dayFrom, int monthTill, int dayTill);

    
    MultiValueMap<Employee, Attendee> getEmployeeforAttendies(List<Attendee> attendees);



    
    boolean checkAge(String year, String month, String day, Clock clock, Integer expectedAge);

    
    boolean checkAge(String year, String month, String day, Integer expectedAge);

    
    Attendee create(Attendee attendee, AttendeeCreationModel attendeeCreationModel, Client client);

    
    Attendee createWithStatusReserved(
            Attendee attendee, String healthProblems,
            SwimmingLevel swimmingLevel, LocalDate birthDate, String city, Client client);

    
    Attendee convertAttendeeToReservedAttendee(Attendee attendee);

    
    List<Attendee> convertAttendeesToReservedAttendees(List<Attendee> attendees);

    
    void save(List<Attendee> attendees);

    
    void sendReservedAttendeeEmail(Client client);
}
