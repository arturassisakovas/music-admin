package com.mAdmin.service;

import com.mAdmin.enumerator.ProgressLevel;
import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import com.mAdmin.model.AttendeeCreationModel;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class AttendeeServiceImpl implements AttendeeService {

    
    private static final int MONTH_AUGUST = 8;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private EmailService emailService;

    
    @Override
    public List<Attendee> getAll() {

        return attendeeRepository.findAll();
    }

    
    @Override
    public Optional<Attendee> getById(Long id) {

        return attendeeRepository.findById(id);
    }

    
    @Override
    public String validateAttribute(String attribute) {

        String validattribute = attribute.trim().replaceAll("\\s+", " ");
        validattribute = WordUtils.capitalizeFully(validattribute);

        return validattribute;
    }

    
    @Override
    public List<Attendee> getAllByNextBirthdayFromNow(int monthFrom, int dayFrom, int monthTill, int dayTill) {
        return attendeeRepository.
                findByBirthdayDateIntervalAndActiveTrue(monthFrom, dayFrom, monthTill, dayTill);
    }

    
    @Override
    public MultiValueMap<Employee, Attendee> getEmployeeforAttendies(List<Attendee> attendees) {

        MultiValueMap<Employee, Attendee> employeeforAttendies = new LinkedMultiValueMap<>();

        for (Attendee attendeeWithBirthday : attendees) {

            Set<Employee> employees = attendeeWithBirthday.getGroup().getEmployees();

            for (Employee coach : employees) {

                if (employeeforAttendies.get(coach) != null) {
                    employeeforAttendies.add(coach, attendeeWithBirthday);
                } else {
                    employeeforAttendies.set(coach, attendeeWithBirthday);
                }

            }
        }

        return employeeforAttendies;

    }

    
    @Override
    public boolean checkAge(String year, String month, String day, Clock clock, Integer expectedAge) {

        int age;
        boolean tooYoung = false;

        if (year != null && month != null && day != null) {
            String birthdateAsString = year + "-" + month + "-" + day;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
            LocalDate birthdate = LocalDate.parse(birthdateAsString, formatter);
            if (LocalDate.now(clock).getMonthValue() == MONTH_AUGUST) {
                age = Period.between(birthdate, LocalDate.now(clock).with(TemporalAdjusters.firstDayOfNextMonth()))
                        .getYears();
            } else {
                age = Period.between(birthdate, LocalDate.now(clock)).getYears();
            }
            if (age < expectedAge) {
                tooYoung = true;
            } else {
                tooYoung = false;
            }
        }
        return tooYoung;
    }

    
    @Override
    public boolean checkAge(String year, String month, String day, Integer expectedAge) {
        return checkAge(year, month, day, Clock.systemDefaultZone(), expectedAge);
    }

    @Override
    public Attendee create(Attendee attendee, AttendeeCreationModel attendeeCreationModel, Client client) {
        Optional<Group> maybeGroup = groupRepository.findById(attendeeCreationModel.getGroupId());
        Group group = maybeGroup.get();

        attendee.setBirthDate(attendeeCreationModel.getBirthDate());
        attendee.setWorkoutStartDate(attendeeCreationModel.getFirstDay());
        attendee.setCity(attendeeCreationModel.getCity());
        attendee.setClient(client);

        if (attendee.getNewGroup() != null) {
            attendee.setNewGroup(group);
        } else {
            attendee.setGroup(group);
        }
        attendee.setSwimmingLevel(attendeeCreationModel.getSwimLevel());
        attendee.setProgressLevel(ProgressLevel.ZERO);

        attendee = attendeeRepository.save(attendee);

        return attendee;
    }

    @Override
    @Transactional
    public Attendee createWithStatusReserved(Attendee reservedAttendee, String healthProblems,
                                             SwimmingLevel swimmingLevel, LocalDate birthDate, String city, Client client) {

        reservedAttendee.setBirthDate(birthDate);
        reservedAttendee.setSwimmingLevel(swimmingLevel);
        reservedAttendee.setCity(city);
        reservedAttendee.setClient(client);
        reservedAttendee.setHealthProblems(healthProblems);
        reservedAttendee.setActive(false);
        reservedAttendee.setProgressLevel(ProgressLevel.ZERO);

        reservedAttendee = attendeeRepository.save(reservedAttendee);

        return reservedAttendee;
    }

    @Override
    @Transactional
    public Attendee convertAttendeeToReservedAttendee(Attendee attendee) {
        if (attendee.getNewGroup() == null) {
            attendee.setGroup(null);
            attendee.setWorkoutStartDate(null);
            attendee.setActive(false);
            attendee.setIsMissing(false);
        } else {
            attendee.setNewGroup(null);
        }
        attendeeRepository.save(attendee);

        return attendee;
    }

    @Override
    @Transactional
    public List<Attendee> convertAttendeesToReservedAttendees(List<Attendee> attendees) {
        List<Attendee> reservedAttendees = new ArrayList<>();

        for (Attendee attendee : attendees) {
            attendee.setGroup(null);
            attendee.setWorkoutStartDate(null);
            attendee.setActive(false);
            attendee.setIsMissing(false);
            reservedAttendees.add(attendee);
        }
        return attendeeRepository.saveAll(reservedAttendees);
    }

    
    @Override
    public void save(List<Attendee> attendees) {

        attendeeRepository.saveAll(attendees);

    }

    
    public void sendReservedAttendeeEmail(Client client) {
        emailService.formAndSendReservedAttendeeMail(client, "mail-templates/attendee-saved-to-reserve-template.html");
    }
}
