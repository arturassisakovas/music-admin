package com.mAdmin.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "group_workouts")

public class GroupWorkout {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "date", nullable = false)
    private LocalDate date;

    
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    
    public GroupWorkout() {

    }

    
    public GroupWorkout(Group group, LocalDate date) {
        this.group = group;
        this.date = date;
    }

    
    public Long getId() {
        return id;
    }

    
    public LocalDate getDate() {
        return date;
    }

    
    public void setDate(LocalDate date) {
        this.date = date;
    }

    
    public Group getGroup() {
        return this.group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
    }

}
