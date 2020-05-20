package com.mAdmin.controller;

import com.mAdmin.enumerator.ProgressLevel;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import com.mAdmin.security.EmployeePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Controller
public class TeacherAttendeesController {

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    private List<Group> coachGroups = new ArrayList<>();

    
    private List<Attendee> groupAttendees;

    
    private Group selectedGroup;

    
    private ProgressLevel selectedProgressLevel;

    
    private boolean enableEdit = false;

    
    public void onLoad() {
        selectedGroup = null;
        groupAttendees = null;
        Long coachId = ((EmployeePrincipal) authenticationFacade.getAuthentication().getPrincipal()).getEmployeeId();
        Employee coach = employeeRepository.getOne(coachId);
        coachGroups.clear();
        if (coach.getGroups().isEmpty()) {
            noGroupsMessage();
        } else {
            LocalDate todayDate = LocalDate.now();
            List<Group> allCoachGroups = new ArrayList<>(coach.getGroups());
            for (Group group : allCoachGroups) {
                if (!attendeeRepository.findByGroup(group).isEmpty()
                        && todayDate.isAfter(group.getStartDate().minusDays(1))
                        && todayDate.isBefore(group.getEndDate().plusDays(1))) {
                    coachGroups.add(group);
                }
            }
            if (!coachGroups.isEmpty()) {
                coachGroups.sort(Comparator.comparing(Group::getName));
            } else {
                noGroupsMessage();
            }
        }
        enableEdit = false;
    }

    
    private void noGroupsMessage() {
        FacesContext context = FacesContext.getCurrentInstance();
        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_WARN, "coach.panel.attendees.noGroups", context);
    }

    
    public void selectGroup() {
        groupAttendees = attendeeRepository.findByGroup(selectedGroup);
        enableEdit = false;
    }

    
    public void enableEditing() {
        enableEdit = true;
    }

    
    public void disableEditing() {
        enableEdit = false;
        groupAttendees = attendeeRepository.findByGroup(selectedGroup);
    }

    
    public void save() {
        FacesContext context = FacesContext.getCurrentInstance();
        groupAttendees = attendeeRepository.saveAll(groupAttendees);
        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "edit.success", context);
        enableEdit = false;
    }

    
    public List<Group> getCoachGroups() {
        return coachGroups;
    }

    
    public void setCoachGroups(List<Group> coachGroups) {
        this.coachGroups = coachGroups;
    }

    
    public Group getSelectedGroup() {
        return selectedGroup;
    }

    
    public void setSelectedGroup(Group selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    
    public List<Attendee> getGroupAttendees() {
        return groupAttendees;
    }

    
    public void setGroupAttendees(List<Attendee> groupAttendees) {
        this.groupAttendees = groupAttendees;
    }

    
    public boolean isEnableEdit() {
        return enableEdit;
    }

    
    public void setEnableEdit(boolean enableEdit) {
        this.enableEdit = enableEdit;
    }

    
    public ProgressLevel getSelectedProgressLevel() {
        return selectedProgressLevel;
    }

    
    public void setSelectedProgressLevel(ProgressLevel selectedProgressLevel) {
        this.selectedProgressLevel = selectedProgressLevel;
    }

}
