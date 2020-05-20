package com.mAdmin.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "attendance")

public class Attendance {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    
    @Column(name = "workout_hour", nullable = false)
    private int workoutHour;

    
    @Column(name = "is_present", nullable = false)
    private Boolean isPresent;

    
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    
    @ManyToOne
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    
    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    
    public int getWorkoutHour() {
        return workoutHour;
    }

    
    public void setWorkoutHour(int workoutHour) {
        this.workoutHour = workoutHour;
    }

    
    public Boolean getIsPresent() {
        return isPresent;
    }

    
    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }

    
    public Group getGroup() {
        return group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
    }

    
    public Attendee getAttendee() {
        return attendee;
    }

    
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    
    public Attendance() {
        super();
    }

}
