package com.mAdmin.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "track_periods")

public class TrackPeriod extends TimeSpan {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "track_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Track track;

    
    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            mappedBy = "trackPeriod")
    @OrderBy(value = "dayOfWeek ASC")
    private Set<TrackWeekday> trackWeekdays = new HashSet<>();

    
    @Override
    public Long getId() {
        return id;
    }

    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    
    public Track getTrack() {
        return track;
    }

    
    public void setTrack(Track track) {
        this.track = track;
    }

    
    public Set<TrackWeekday> getTrackWeekdays() {
        return trackWeekdays;
    }

    
    public void setTrackWeekdays(Set<TrackWeekday> trackWeekdays) {
        this.trackWeekdays = trackWeekdays;
    }

    
    public TrackPeriod(Track track, LocalDate startDate, LocalDate endDate) {
        super();
        this.track = track;
        setStartDate(startDate);
        setEndDate(endDate);
    }

    
    public TrackPeriod() {
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof TrackPeriod) && (id != null)) {
            return id.equals(((TrackPeriod) other).id);
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
}
