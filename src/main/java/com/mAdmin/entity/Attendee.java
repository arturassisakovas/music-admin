package com.mAdmin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.Period;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.ProgressLevel;
import com.mAdmin.enumerator.SwimmingLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "attendees")
public class Attendee {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "is_active")
    private Boolean active;

    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    
    @Column(name = "name")
    private String name;

    
    @Column(name = "surname")
    private String surname;

    
    @Column(name = "health_problems")
    private String healthProblems;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "swimming_level")
    private SwimmingLevel swimmingLevel;

    
    @Column(name = "birth_date")
    private LocalDate birthDate;

    
    @Column(name = "gender")
    private String gender;

    
    @Column(name = "city")
    private String city;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    
    @ManyToOne
    @JoinColumn(name = "new_group_id")
    private Group newGroup;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "progress_level")
    private ProgressLevel progressLevel;

    
    @Column(name = "is_missing")
    private boolean isMissing;

    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "attendee")
    private Set<Attendance> attendance = new HashSet<>();

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attendee")
    private List<Contract> contracts;

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attendee")
    private List<Subscription> subscriptions;

    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "attendee")
    private AttendeeNonPaid attendeeNonPaid;

    
    @Column(name = "workout_start_date", nullable = false)
    private LocalDate workoutStartDate;

    
    public Integer getAge() {

        if (getBirthDate() == null) {
            return null;
        }

        LocalDate today = LocalDate.now();

        return Period.between(getBirthDate(), today).getYears();

    }

    
    public Boolean getActive() {
        return active;
    }

    
    public void setActive(Boolean active) {
        this.active = active;
    }

    
    public Long getId() {
        return this.id;
    }

    
    public Client getClient() {
        return this.client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public String getName() {
        return this.name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getSurname() {
        return this.surname;
    }

    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    
    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    
    public String getGender() {
        return this.gender;
    }

    
    public void setGender(String gender) {
        this.gender = gender;
    }

    
    public String getCity() {
        return city;
    }

    
    public void setCity(String city) {
        this.city = city;
    }

    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public void setProgressLevel(ProgressLevel progressLevel) {
        this.progressLevel = progressLevel;
    }

    
    public void setSwimmingLevel(SwimmingLevel swimLevel) {
        this.swimmingLevel = swimLevel;
    }
    
    public SwimmingLevel getSwimmingLevel() {
        return swimmingLevel;
    }

    
    public String getHealthProblems() {
        return healthProblems;
    }

    
    public void setHealthProblems(String healthProblems) {
        this.healthProblems = healthProblems;
    }

    
    public Group getGroup() {
        return this.group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
    }

    
    public Group getNewGroup() {
        return newGroup;
    }

    
    public void setNewGroup(Group newGroup) {
        this.newGroup = newGroup;
    }

    
    public ProgressLevel getProgressLevel() {
        return progressLevel;
    }

    
    public Boolean getIsMissing() {
        return isMissing;
    }

    
    public void setIsMissing(Boolean isMissing) {
        this.isMissing = isMissing;
    }

    
    public Set<Attendance> getAttendance() {
        return attendance;
    }

    
    public void setAttendance(Set<Attendance> attendance) {
        this.attendance = attendance;
    }

    
    public Attendee() {
        setActive(true);
    }

    
    public Attendee(Group group) {
        this();
        this.group = group;
    }

    
    public Contract getContract() {
        List<Contract> filteredContracts = new ArrayList<>();
        List<Contract> contracts = this.contracts;
        if (!contracts.isEmpty()) {
            for (Contract contract:contracts) {
                if (contract.getType() != ContractType.INVALID) {
                    filteredContracts.add(contract);
                }
            }
            if (!filteredContracts.isEmpty()) {
                Collections.reverse(filteredContracts);
                return filteredContracts.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    
    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    
    public AttendeeNonPaid getAttendeeNonPaid() {
        return attendeeNonPaid;
    }

    
    public void setAttendeeNonPaid(AttendeeNonPaid attendeeNonPaid) {
        this.attendeeNonPaid = attendeeNonPaid;
    }

    
    public LocalDate getWorkoutStartDate() {
        return workoutStartDate;
    }

    
    public void setWorkoutStartDate(LocalDate workoutStartDate) {
        this.workoutStartDate = workoutStartDate;
    }

    
    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    
    public Attendee(String name, String surname, LocalDate birthDate, String gender) {
        this();
        setName(name);
        setSurname(surname);
        setBirthDate(birthDate);
        setGender(gender);
    }

    
    public Attendee(String name, String surname, LocalDate birthDate, String gender, String city,
            SwimmingLevel swimmingLevel, String healthProblems) {
        this(name, surname, birthDate, gender);
        setCity(city);
        setSwimmingLevel(swimmingLevel);
        setHealthProblems(healthProblems);
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Attendee) && (getId() != null)) {
            return getId().equals(((Attendee) other).getId());
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return this.getClass().hashCode() + getId().hashCode();
        }
        return super.hashCode();
    }
}
