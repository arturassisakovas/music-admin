package com.mAdmin.entity;

import com.mAdmin.enumerator.AgeGroup;
import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.enumerator.WorkoutsPerWeek;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "groups")

public class Group extends TimeSpan {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "name", nullable = false)
    private String name;

    
    @Column(name = "num_of_attendees", nullable = false)
    private Integer numOfAttendees;

    
    @Column(name = "max_attendees", nullable = false)
    private Integer maxAttendees;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
    private Set<Attendee> attendees = new HashSet<>();

    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "newGroup")
    private Set<Attendee> newAttendees = new HashSet<>();

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<AttendeeInSession> attendeesInSession = new HashSet<>();

    
    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    private AgeGroup ageGroup;

    

    @Column(name = "workouts_per_week")
    private WorkoutsPerWeek workoutsPerWeek;

    
    @ManyToMany
    @JoinTable(name = "employee_group", joinColumns = {@JoinColumn(name = "group_id")}, inverseJoinColumns = {
            @JoinColumn(name = "employee_id")})
    private Set<Employee> employees = new HashSet<>();

    
    @ManyToOne
    @JoinColumn(name = "pool_id")
    private Pool pool;

    
    @ManyToOne
    @JoinColumn(name = "track_period_id")
    private TrackPeriod trackPeriod;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "swimming_level")
    private SwimmingLevel swimmingLevel;

    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
    private List<GroupWorkout> groupWorkouts = new ArrayList<>();

    
    @Column(name = "public")
    private Boolean isPublic;

    
    @Formula("((select count(*) from attendees a where a.group_id = id) "
            + "+ (select count(*) from attendees a where a.new_group_id = id) "
            + "+ (select count(*) from attendees_in_session a where a.group_id = id))")
    private int totalNumOfAttendees;

    
    @Formula("(((select count(*) from attendees a where a.group_id = id) "
            + "+ (select count(*) from attendees a where a.new_group_id = id) "
            + "+ (select count(*) from attendees_in_session a where a.group_id = id)) "
            + "/ max_attendees)")
    private int totalNumOfAttendeesRatio;

    
    public int getTotalNumOfAttendees() {
        return totalNumOfAttendees;
    }

    
    public int getTotalNumOfAttendeesRatio() {
        return totalNumOfAttendeesRatio;
    }

    
    public int getVisibleNumOfAttendees() {
        int visibleNumberOfAttendees = totalNumOfAttendees;

        if (totalNumOfAttendees >= numOfAttendees) {
            visibleNumberOfAttendees = numOfAttendees;
        }
        return visibleNumberOfAttendees;
    }

    
    public int getVisibleNumOfAttendeesPercentage() {

        return getPercentage(getVisibleNumOfAttendees(), numOfAttendees);

    }

    
    public int getHiddenNumOfAttendees() {

        int hiddenNumOfAttendees = totalNumOfAttendees - numOfAttendees;

        if (hiddenNumOfAttendees >= 1) {
            return hiddenNumOfAttendees;
        }

        return 0;

    }

    
    public int getHiddenNumOfAttendeesPercentage() {

        return getPercentage(getHiddenNumOfAttendees(), getNumOfHiddenPlaces());

    }

    
    public int getNumOfHiddenPlaces() {
        return maxAttendees - numOfAttendees;
    }

    
    public int getPercentage(int numerator, int denominator) {

        final int oneHundredPercent = 100;

        double tempPercentage = (double) numerator / (double) denominator * oneHundredPercent;

        return (int) Math.round(tempPercentage);
    }

    
    @Override
    public Long getId() {
        return id;
    }

    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getName() {
        return this.name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public Integer getNumOfAttendees() {
        return this.numOfAttendees;
    }

    
    public void setNumberOfAttendees(Integer attendees) {
        this.numOfAttendees = attendees;
    }

    
    public Integer getMaxAttendees() {
        return this.maxAttendees;
    }

    
    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    
    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    
    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    @Override
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    
    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public AgeGroup getAgeGroup() {
        return this.ageGroup;
    }

    
    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    
    public Set<Employee> getEmployees() {
        return this.employees;
    }

    
    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    
    public Pool getPool() {
        return this.pool;
    }

    
    public void setPool(Pool pool) {
        this.pool = pool;
    }

    
    public TrackPeriod getTrackPeriod() {
        return this.trackPeriod;
    }

    
    public void setTrackPeriod(TrackPeriod trackPeriod) {
        this.trackPeriod = trackPeriod;
    }

    
    public SwimmingLevel getSwimmingLevel() {
        return this.swimmingLevel;
    }

    
    public void setSwimmingLevel(SwimmingLevel swimmingLevel) {
        this.swimmingLevel = swimmingLevel;
    }

    
    public Set<AttendeeInSession> getAttendeesInSession() {
        return attendeesInSession;
    }

    
    public void setAttendeesInSession(Set<AttendeeInSession> attendeesInSession) {
        this.attendeesInSession = attendeesInSession;
    }

    
    public WorkoutsPerWeek getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    
    public void setWorkoutsPerWeek(WorkoutsPerWeek workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    
    public Group() {
    }

    
    public Group(String name, int numOfAttendees, int maxAttendees, LocalDate startDate, LocalDate endDate) {
        super();
        this.name = name;
        this.numOfAttendees = numOfAttendees;
        this.maxAttendees = maxAttendees;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    
    public Group(String name, int numOfAttendees, int maxAttendees, TrackPeriod trackPeriod, LocalDate startDate,
            LocalDate endDate) {
        super();
        this.name = name;
        this.numOfAttendees = numOfAttendees;
        this.maxAttendees = maxAttendees;
        this.trackPeriod = trackPeriod;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    
    public Group(int totalNumOfAttendees) {
        this.totalNumOfAttendees = totalNumOfAttendees;
    }

    
    public Set<Attendee> getAttendees() {
        return attendees;
    }

    
    public void setAttendees(Set<Attendee> attendees) {
        this.attendees = attendees;
    }

    
    public Set<Attendee> getNewAttendees() {
        return newAttendees;
    }

    
    public void setNewAttendees(Set<Attendee> newAttendees) {
        this.newAttendees = newAttendees;
    }

    
    public List<GroupWorkout> getGroupWorkouts() {
        return groupWorkouts;
    }

    
    public void setGroupWorkouts(List<GroupWorkout> groupWorkouts) {
        this.groupWorkouts = groupWorkouts;
    }

    
    public Boolean getIsPublic() {
        return isPublic;
    }

    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

}
