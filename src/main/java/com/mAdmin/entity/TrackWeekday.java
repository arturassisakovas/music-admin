package com.mAdmin.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.mAdmin.enumerator.DayOfWeek;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "track_weekdays")

public class TrackWeekday {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "track_period_id", nullable = false)
    private TrackPeriod trackPeriod;

    
    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            mappedBy = "trackWeekday")
    @OrderBy(value = "startHour ASC")
    private Set<TrackWorkingHour> trackWorkingHours = new HashSet<>();

    
    @Column(name = "day_of_week", nullable = false)
    @Enumerated
    private DayOfWeek dayOfWeek;

    
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

    
    public TrackPeriod getTrackPeriod() {
        return trackPeriod;
    }

    
    public void setTrackPeriod(TrackPeriod trackPeriod) {
        this.trackPeriod = trackPeriod;
    }

    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    
    public Set<TrackWorkingHour> getTrackWorkingHours() {
        return trackWorkingHours;
    }

    
    public void setTrackWorkingHours(Set<TrackWorkingHour> trackWorkingHours) {
        this.trackWorkingHours = trackWorkingHours;
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

    
    public TrackWeekday(Long id, TrackPeriod trackPeriod, DayOfWeek dayOfWeek) {
        super();
        this.id = id;
        this.trackPeriod = trackPeriod;
        this.dayOfWeek = dayOfWeek;
    }

    
    public TrackWeekday() {
        super();
    }

    
    public TrackWeekday(TrackWeekday one, DayOfWeek selectedDay) {
    }
}
