package com.mAdmin.controller;

import com.mAdmin.enumerator.WorkoutsPerWeek;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.repository.CabinetRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Pool;
import com.mAdmin.entity.Track;
import com.mAdmin.entity.TrackWorkingHour;
import com.mAdmin.service.InvoiceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
@Scope(value = "view")
public class TimetablesManagementController {

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private PeriodRepository periodRepository;

    
    @Autowired
    private CabinetRepository cabinetRepository;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    
    private List<Group> groups;

    
    private List<Group> selectedGroups;

    
    private List<Period> periods;

    
    private List<WorkoutsPerWeek> workoutsPerWeek;

    
    private List<Group> filteredGroups;

    
    private List<Pool> pools;

    
    private List<Pool> selectedPools = new ArrayList<>();

    
    private Period selectedPeriod = null;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @PostConstruct
    public void init() {
        periods = periodRepository.findAll();
        Collections.reverse(periods);
        setSelectedPeriod(periodRepository.findByDateBetweenStartAndEndDates(LocalDate.now()));
        filterGroups();
        workoutsPerWeek = Arrays.asList(WorkoutsPerWeek.values());
    }

    
    public void filterGroups() {
        selectedPools = new ArrayList<>();
        if (selectedPeriod != null) {
            Optional<Period> currentPeriod = periodRepository.findById(selectedPeriod.getId());
            if (currentPeriod.isPresent()) {
                pools = currentPeriod.get().getSeason().getPools();
                groups = groupRepository.findByStartDateAndEndDateFitInPeriod(currentPeriod.get().getStartDate(),
                        currentPeriod.get().getEndDate());
                Collections.reverse(groups);
                selectedGroups = groups.stream().filter(Group::getIsPublic).collect(Collectors.toList());
            }
        }
    }

    
    public void saveGroups() {
        FacesContext context = FacesContext.getCurrentInstance();
        groups.forEach(sg -> {
            if (selectedGroups.contains(sg)) {
                sg.setIsPublic(true);
            } else {
                sg.setIsPublic(false);
            }
        });
        groupRepository.saveAll(groups);

        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "timetable.group.activated", context);
    }

    
    @Transactional
    public void deleteGroup(Group group) {
        if (filteredGroups != null) {
            filteredGroups.removeIf(g -> g.getId().equals(group.getId()));
        }
        groups.removeIf(g -> g.getId().equals(group.getId()));
        List<TrackWorkingHour> trackWorkingHours = cabinetWorkingHourRepository.findByGroup(group);
        trackWorkingHours.forEach(hour -> hour.setGroup(null));
        cabinetWorkingHourRepository.saveAll(trackWorkingHours);
        groupRepository.delete(group);
    }

    
    public boolean disableGroupPublicity(Group group) {
        List<InvoiceRecord> invoiceRecords = invoiceRecordRepository.findByGroup(group);
        return !invoiceRecords.isEmpty() || !group.getAttendees().isEmpty();
    }

    
    public Track collectTrack(Long trackId) {
        return cabinetRepository.getOne(trackId);
    }

    
    public Set<Employee> collectEmployees(Long groupId) {
        if (groupId != null) {
            Optional<Group> group = groupRepository.findById(groupId);
            if (group.isPresent()) {
                return group.get().getEmployees();
            }
        }
        return null;
    }

    
    public List<Group> getGroups() {
        return groups;
    }

    
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    
    public List<Period> getPeriods() {
        return periods;
    }

    
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    
    public Period getSelectedPeriod() {
        return selectedPeriod;
    }

    
    public void setSelectedPeriod(Period selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }

    
    public List<Group> getSelectedGroups() {
        return selectedGroups;
    }

    
    public void setSelectedGroups(List<Group> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    
    public List<WorkoutsPerWeek> getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    
    public void setWorkoutsPerWeek(List<WorkoutsPerWeek> workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    
    public List<Group> getFilteredGroups() {
        return filteredGroups;
    }

    
    public void setFilteredGroups(List<Group> filteredGroups) {
        this.filteredGroups = filteredGroups;
    }

    
    public List<Pool> getPools() {
        return pools;
    }

    
    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    
    public List<Pool> getSelectedPools() {
        return selectedPools;
    }

    
    public void setSelectedPools(List<Pool> selectedPools) {
        this.selectedPools = selectedPools;
    }

}
