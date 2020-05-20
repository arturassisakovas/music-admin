package com.mAdmin.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mAdmin.enumerator.DayOfWeek;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "track_working_hours")

public class TrackWorkingHour {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "track_weekday_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TrackWeekday trackWeekday;

    
    @Column(name = "start_hour", nullable = false)
    private int startHour;

    
    @Column(name = "end_hour", nullable = false)
    private int endHour;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public TrackWeekday getTrackWeekday() {
        return trackWeekday;
    }

    
    public DayOfWeek getDayOfWeek() {
        return trackWeekday.getDayOfWeek();
    }

    
    public void setTrackWeekday(TrackWeekday trackWeekday) {
        this.trackWeekday = trackWeekday;
    }

    
    public TrackWorkingHour(TrackWeekday trackWeekday, Group group, int startHour, int endHour) {
        super();
        this.trackWeekday = trackWeekday;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    
    public int getStartHour() {
        return startHour;
    }

    
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    
    public int getEndHour() {
        return endHour;
    }

    
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    
    public Group getGroup() {
        return group;
    }

    
    public void setGroup(Group group) {
        this.group = group;
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

    
    public TrackWorkingHour() {
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof TrackWorkingHour) && (id != null)) {
            return id.equals(((TrackWorkingHour) other).id);
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return (this.getClass().hashCode() + id.hashCode());
        }
        return super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

}
